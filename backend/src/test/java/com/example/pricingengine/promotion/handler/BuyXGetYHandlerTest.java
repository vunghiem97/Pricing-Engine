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
class BuyXGetYHandlerTest {

    private Promotion buildPromotion(int buyQty, int freeQty) {
        return Promotion.builder()
                .name("Buy " + buyQty + " Get " + freeQty)
                .type(PromotionType.BUY_X_GET_Y)
                .value(new BigDecimal(buyQty))
                .freeQuantity(freeQty)
                .active(true)
                .build();
    }

    @Test
    void applyPromotion_buy2Get1_sixItems_twoFreeCycles() {
        // cycle=3, qty=6 => 2 cycles => 2 free units
        Promotion promotion = buildPromotion(2, 1);
        BuyXGetYHandler handler = new BuyXGetYHandler(promotion);
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-A").price(new BigDecimal("50.00")).quantity(6).build();
        PromotionContext context = new PromotionContext(
                CustomerType.NORMAL, List.of(item), null, new BigDecimal("300.00"));

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("100.00");
        assertThat(context.getBreakdowns()).hasSize(1);
        assertThat(context.getBreakdowns().get(0).getPromotionType()).isEqualTo("BUY_X_GET_Y");
    }

    @Test
    void applyPromotion_buy2Get1_twoItems_noDiscount() {
        Promotion promotion = buildPromotion(2, 1);
        BuyXGetYHandler handler = new BuyXGetYHandler(promotion);
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-A").price(new BigDecimal("50.00")).quantity(2).build();
        PromotionContext context = new PromotionContext(
                CustomerType.NORMAL, List.of(item), null, new BigDecimal("100.00"));

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
    }

    @Test
    void applyPromotion_buy2Get1_threeItems_oneCycle() {
        Promotion promotion = buildPromotion(2, 1);
        BuyXGetYHandler handler = new BuyXGetYHandler(promotion);
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-B").price(new BigDecimal("30.00")).quantity(3).build();
        PromotionContext context = new PromotionContext(
                CustomerType.NORMAL, List.of(item), null, new BigDecimal("90.00"));

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("30.00");
    }

    @Test
    void applyPromotion_buy2Get1_multipleItems_sumsDiscounts() {
        // Item A: qty=3 => price=20 => 1 free = 20
        // Item B: qty=6 => price=10 => 2 free = 20
        Promotion promotion = buildPromotion(2, 1);
        BuyXGetYHandler handler = new BuyXGetYHandler(promotion);
        OrderItemRequest itemA = OrderItemRequest.builder()
                .sku("SKU-A").price(new BigDecimal("20.00")).quantity(3).build();
        OrderItemRequest itemB = OrderItemRequest.builder()
                .sku("SKU-B").price(new BigDecimal("10.00")).quantity(6).build();
        PromotionContext context = new PromotionContext(
                CustomerType.NORMAL, List.of(itemA, itemB), null, new BigDecimal("120.00"));

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("40.00");
    }

    @Test
    void applyPromotion_buy3Get2_fiveItems_oneCycle() {
        Promotion promotion = buildPromotion(3, 2);
        BuyXGetYHandler handler = new BuyXGetYHandler(promotion);
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-C").price(new BigDecimal("40.00")).quantity(5).build();
        PromotionContext context = new PromotionContext(
                CustomerType.NORMAL, List.of(item), null, new BigDecimal("200.00"));

        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("80.00");
    }
}

