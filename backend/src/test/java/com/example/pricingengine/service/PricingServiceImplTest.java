package com.example.pricingengine.service;

import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.dto.request.OrderRequest;
import com.example.pricingengine.dto.response.PricingResponse;
import com.example.pricingengine.promotion.AbstractPromotionHandler;
import com.example.pricingengine.promotion.PromotionChainFactory;
import com.example.pricingengine.promotion.PromotionContext;
import com.example.pricingengine.promotion.PromotionHandler;
import com.example.pricingengine.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private PromotionChainFactory promotionChainFactory;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PricingServiceImpl pricingService;

    /** No-op handler that adds no discount */
    private PromotionHandler noOpHandler() {
        return new AbstractPromotionHandler() {
            @Override
            protected void applyPromotion(PromotionContext context) { /* no-op */ }
        };
    }

    /** Handler that adds a fixed discount amount */
    private PromotionHandler fixedDiscountHandler(BigDecimal amount) {
        return new AbstractPromotionHandler() {
            @Override
            protected void applyPromotion(PromotionContext context) {
                context.addDiscount("Test Discount", "PERCENTAGE_DISCOUNT", amount);
            }
        };
    }

    private OrderRequest singleItemRequest(String price, int qty, CustomerType type) {
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-001").price(new BigDecimal(price)).quantity(qty).build();
        return OrderRequest.builder()
                .customerType(type)
                .items(List.of(item))
                .couponCode(null)
                .build();
    }

    @Test
    void calculate_noDiscount_finalPriceEqualsSubtotal() {
        when(promotionChainFactory.buildChain()).thenReturn(noOpHandler());

        OrderRequest request = singleItemRequest("100.00", 2, CustomerType.NORMAL);
        PricingResponse response = pricingService.calculate(request);

        assertThat(response.getSubtotal()).isEqualByComparingTo("200.00");
        assertThat(response.getDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getFinalPrice()).isEqualByComparingTo("200.00");
        assertThat(response.getDiscountBreakdown()).isEmpty();
    }

    @Test
    void calculate_withDiscount_correctFinalPrice() {
        when(promotionChainFactory.buildChain()).thenReturn(fixedDiscountHandler(new BigDecimal("30.00")));

        OrderRequest request = singleItemRequest("50.00", 4, CustomerType.NORMAL);
        PricingResponse response = pricingService.calculate(request);

        assertThat(response.getSubtotal()).isEqualByComparingTo("200.00");
        assertThat(response.getDiscount()).isEqualByComparingTo("30.00");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("170.00");
    }

    @Test
    void calculate_discountExceedsSubtotal_finalPriceIsZero() {
        when(promotionChainFactory.buildChain()).thenReturn(fixedDiscountHandler(new BigDecimal("999.00")));

        OrderRequest request = singleItemRequest("50.00", 1, CustomerType.NORMAL);
        PricingResponse response = pricingService.calculate(request);

        assertThat(response.getSubtotal()).isEqualByComparingTo("50.00");
        assertThat(response.getDiscount()).isEqualByComparingTo("50.00");
        assertThat(response.getFinalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculate_multipleItems_correctSubtotal() {
        when(promotionChainFactory.buildChain()).thenReturn(noOpHandler());

        OrderItemRequest item1 = OrderItemRequest.builder()
                .sku("A").price(new BigDecimal("10.00")).quantity(3).build();
        OrderItemRequest item2 = OrderItemRequest.builder()
                .sku("B").price(new BigDecimal("25.00")).quantity(2).build();
        OrderRequest request = OrderRequest.builder()
                .customerType(CustomerType.NORMAL)
                .items(List.of(item1, item2))
                .build();

        PricingResponse response = pricingService.calculate(request);

        // 10*3 + 25*2 = 30 + 50 = 80
        assertThat(response.getSubtotal()).isEqualByComparingTo("80.00");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("80.00");
    }

    @Test
    void calculate_persistsOrderToRepository() {
        when(promotionChainFactory.buildChain()).thenReturn(fixedDiscountHandler(new BigDecimal("10.00")));

        OrderRequest request = singleItemRequest("100.00", 1, CustomerType.VIP);
        pricingService.calculate(request);

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void calculate_discountBreakdown_includedInResponse() {
        PromotionHandler customHandler = new AbstractPromotionHandler() {
            @Override
            protected void applyPromotion(PromotionContext context) {
                context.addDiscount("Summer Sale", "PERCENTAGE_DISCOUNT", new BigDecimal("15.00"));
                context.addDiscount("VIP Bonus", "VIP_DISCOUNT", new BigDecimal("5.00"));
            }
        };
        when(promotionChainFactory.buildChain()).thenReturn(customHandler);

        OrderRequest request = singleItemRequest("100.00", 1, CustomerType.VIP);
        PricingResponse response = pricingService.calculate(request);

        assertThat(response.getDiscountBreakdown()).hasSize(2);
        assertThat(response.getDiscountBreakdown())
                .extracting(PricingResponse.DiscountBreakdown::getPromotionName)
                .containsExactly("Summer Sale", "VIP Bonus");
    }

    @Test
    void calculate_couponCode_forwardedToContext() {
        ArgumentCaptor<PromotionContext> contextCaptor = ArgumentCaptor.forClass(PromotionContext.class);
        PromotionHandler capturingHandler = mock(PromotionHandler.class);
        when(promotionChainFactory.buildChain()).thenReturn(capturingHandler);

        OrderRequest request = OrderRequest.builder()
                .customerType(CustomerType.NORMAL)
                .items(List.of(OrderItemRequest.builder()
                        .sku("SKU-X").price(new BigDecimal("50.00")).quantity(1).build()))
                .couponCode("TESTCODE")
                .build();
        pricingService.calculate(request);

        verify(capturingHandler).handle(contextCaptor.capture());
        assertThat(contextCaptor.getValue().getCouponCode()).isEqualTo("TESTCODE");
    }
}



