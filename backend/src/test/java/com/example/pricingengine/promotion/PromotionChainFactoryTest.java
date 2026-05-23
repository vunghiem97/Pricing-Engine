package com.example.pricingengine.promotion;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.domain.enums.PromotionType;
import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.repository.CouponRepository;
import com.example.pricingengine.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PromotionChainFactoryTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private PromotionChainFactory factory;

    private PromotionContext buildContext(CustomerType type, String subtotal, String couponCode) {
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-001").price(new BigDecimal(subtotal)).quantity(1).build();
        return new PromotionContext(type, List.of(item), couponCode, new BigDecimal(subtotal));
    }

    @Test
    void buildChain_noPromotions_noCoupon_zeroDiscount() {
        when(promotionRepository.findByActiveTrue()).thenReturn(List.of());
        when(couponRepository.findByCodeAndActiveTrue(any())).thenReturn(Optional.empty());

        PromotionHandler chain = factory.buildChain();
        PromotionContext context = buildContext(CustomerType.NORMAL, "100.00", "BADCODE");
        chain.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void buildChain_singlePercentagePromotion_appliesDiscount() {
        Promotion promo = Promotion.builder()
                .name("10% Off").type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(new BigDecimal("10")).active(true).build();
        when(promotionRepository.findByActiveTrue()).thenReturn(List.of(promo));
        when(couponRepository.findByCodeAndActiveTrue(any())).thenReturn(Optional.empty());

        PromotionHandler chain = factory.buildChain();
        PromotionContext context = buildContext(CustomerType.NORMAL, "200.00", null);
        chain.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("20.00");
    }

    @Test
    void buildChain_vipPromotion_normalCustomer_noDiscount() {
        Promotion vipPromo = Promotion.builder()
                .name("VIP 5%").type(PromotionType.VIP_DISCOUNT)
                .value(new BigDecimal("5")).active(true).build();
        when(promotionRepository.findByActiveTrue()).thenReturn(List.of(vipPromo));
        when(couponRepository.findByCodeAndActiveTrue(any())).thenReturn(Optional.empty());

        PromotionHandler chain = factory.buildChain();
        PromotionContext context = buildContext(CustomerType.NORMAL, "100.00", null);
        chain.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void buildChain_couponTypePromotion_ignoredFromChain() {
        Promotion couponTypePromo = Promotion.builder()
                .name("Coupon Promo").type(PromotionType.COUPON)
                .value(BigDecimal.ZERO).active(true).build();
        when(promotionRepository.findByActiveTrue()).thenReturn(List.of(couponTypePromo));
        when(couponRepository.findByCodeAndActiveTrue(any())).thenReturn(Optional.empty());

        PromotionHandler chain = factory.buildChain();
        PromotionContext context = buildContext(CustomerType.NORMAL, "100.00", null);
        chain.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void buildChain_multiplePromotions_accumulatesDiscounts() {
        Promotion percentagePromo = Promotion.builder()
                .name("10% Off").type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(new BigDecimal("10")).active(true).build();
        Promotion vipPromo = Promotion.builder()
                .name("VIP 5%").type(PromotionType.VIP_DISCOUNT)
                .value(new BigDecimal("5")).active(true).build();
        when(promotionRepository.findByActiveTrue()).thenReturn(List.of(percentagePromo, vipPromo));
        when(couponRepository.findByCodeAndActiveTrue(any())).thenReturn(Optional.empty());

        PromotionHandler chain = factory.buildChain();
        // VIP customer: 10% + 5% = 15% of 200 = 30
        PromotionContext context = buildContext(CustomerType.VIP, "200.00", null);
        chain.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("30.00");
        assertThat(context.getBreakdowns()).hasSize(2);
    }
}
