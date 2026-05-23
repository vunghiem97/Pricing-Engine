package com.example.pricingengine.promotion;

import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.dto.response.PricingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
class PromotionContextTest {

    private PromotionContext context;

    @BeforeEach
    void setUp() {
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-001").price(new BigDecimal("100.00")).quantity(2).build();
        context = new PromotionContext(
                CustomerType.NORMAL,
                List.of(item),
                null,
                new BigDecimal("200.00")
        );
    }

    @Test
    void initialState() {
        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
    }

    @Test
    void addDiscount_positive_accumulatesAndAddsBreakdown() {
        context.addDiscount("10% Off", "PERCENTAGE_DISCOUNT", new BigDecimal("20.00"));

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("20.00");
        assertThat(context.getBreakdowns()).hasSize(1);

        PricingResponse.DiscountBreakdown breakdown = context.getBreakdowns().get(0);
        assertThat(breakdown.getPromotionName()).isEqualTo("10% Off");
        assertThat(breakdown.getPromotionType()).isEqualTo("PERCENTAGE_DISCOUNT");
        assertThat(breakdown.getDiscountAmount()).isEqualByComparingTo("20.00");
    }

    @Test
    void addDiscount_zero_doesNotAddBreakdown() {
        context.addDiscount("No Discount", "PERCENTAGE_DISCOUNT", BigDecimal.ZERO);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
    }

    @Test
    void addDiscount_multiple_accumulatesAll() {
        context.addDiscount("10% Off", "PERCENTAGE_DISCOUNT", new BigDecimal("20.00"));
        context.addDiscount("VIP", "VIP_DISCOUNT", new BigDecimal("10.00"));

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("30.00");
        assertThat(context.getBreakdowns()).hasSize(2);
    }

    @Test
    void getRemainingAmount_returnsCorrectRemainder() {
        context.addDiscount("Test", "PERCENTAGE_DISCOUNT", new BigDecimal("50.00"));
        assertThat(context.getRemainingAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    void getRemainingAmount_neverNegative() {
        context.addDiscount("Big Discount", "COUPON", new BigDecimal("300.00"));
        assertThat(context.getRemainingAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

