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
class VipDiscountHandlerTest {

    private Promotion buildPromotion(String percentage) {
        return Promotion.builder()
                .name("VIP 5% Extra")
                .type(PromotionType.VIP_DISCOUNT)
                .value(new BigDecimal(percentage))
                .active(true)
                .build();
    }

    private PromotionContext buildContext(CustomerType customerType, String subtotal) {
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-001").price(new BigDecimal(subtotal)).quantity(1).build();
        return new PromotionContext(customerType, List.of(item), null, new BigDecimal(subtotal));
    }

    @Test
    void applyPromotion_vipCustomer_getsDiscount() {
        Promotion promotion = buildPromotion("5");
        VipDiscountHandler handler = new VipDiscountHandler(promotion);
        PromotionContext context = buildContext(CustomerType.VIP, "200.00");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("10.00");
        assertThat(context.getBreakdowns()).hasSize(1);
        assertThat(context.getBreakdowns().get(0).getPromotionType()).isEqualTo("VIP_DISCOUNT");
    }

    @Test
    void applyPromotion_normalCustomer_noDiscount() {
        Promotion promotion = buildPromotion("5");
        VipDiscountHandler handler = new VipDiscountHandler(promotion);
        PromotionContext context = buildContext(CustomerType.NORMAL, "200.00");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
    }

    @Test
    void applyPromotion_vipCustomer_10percent_300subtotal() {
        Promotion promotion = buildPromotion("10");
        VipDiscountHandler handler = new VipDiscountHandler(promotion);
        PromotionContext context = buildContext(CustomerType.VIP, "300.00");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("30.00");
    }

    @Test
    void applyPromotion_vipCustomer_roundsHalfUp() {
        // 33.33 * 7% / 100 = 2.3331 => 2.33
        Promotion promotion = buildPromotion("7");
        VipDiscountHandler handler = new VipDiscountHandler(promotion);
        PromotionContext context = buildContext(CustomerType.VIP, "33.33");

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("2.33");
    }
}

