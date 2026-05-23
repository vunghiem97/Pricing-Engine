package com.example.pricingengine.promotion.handler;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.domain.enums.PromotionType;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.promotion.PromotionContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
class PercentageDiscountHandlerTest {

    private PromotionContext buildContext(String subtotal) {
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-001").price(new BigDecimal(subtotal)).quantity(1).build();
        return new PromotionContext(CustomerType.NORMAL, List.of(item), null, new BigDecimal(subtotal));
    }

    @Test
    void applyPromotion_10percent_correctDiscount() {
        Promotion promotion = Promotion.builder()
                .name("10% Off").type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(new BigDecimal("10")).active(true).build();
        PercentageDiscountHandler handler = new PercentageDiscountHandler(promotion);
        PromotionContext context = buildContext("200.00");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("20.00");
        assertThat(context.getBreakdowns()).hasSize(1);
        assertThat(context.getBreakdowns().get(0).getPromotionType()).isEqualTo("PERCENTAGE_DISCOUNT");
        assertThat(context.getBreakdowns().get(0).getPromotionName()).isEqualTo("10% Off");
    }

    @Test
    void applyPromotion_zeroPercent_noDiscount() {
        Promotion promotion = Promotion.builder()
                .name("0% Off").type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(BigDecimal.ZERO).active(true).build();
        PercentageDiscountHandler handler = new PercentageDiscountHandler(promotion);
        PromotionContext context = buildContext("100.00");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
    }

    @Test
    void applyPromotion_15percent_roundsCorrectly() {
        Promotion promotion = Promotion.builder()
                .name("15% Off").type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(new BigDecimal("15")).active(true).build();
        PercentageDiscountHandler handler = new PercentageDiscountHandler(promotion);
        PromotionContext context = buildContext("150.00");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("22.50");
    }

    @Test
    void applyPromotion_irregularPercent_roundsHalfUp() {
        Promotion promotion = Promotion.builder()
                .name("33.33% Off").type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(new BigDecimal("33.33")).active(true).build();
        PercentageDiscountHandler handler = new PercentageDiscountHandler(promotion);
        PromotionContext context = buildContext("10.00");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("3.33");
    }
}

