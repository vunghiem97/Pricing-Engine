package com.example.pricingengine.promotion.handler;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.promotion.AbstractPromotionHandler;
import com.example.pricingengine.promotion.PromotionContext;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy: Applies a flat percentage discount on the order subtotal
 */
public class PercentageDiscountHandler extends AbstractPromotionHandler {

    private final Promotion promotion;

    public PercentageDiscountHandler(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    protected void applyPromotion(PromotionContext context) {
        BigDecimal percentage = promotion.getValue();
        BigDecimal discountAmount = context.getSubtotal()
                .multiply(percentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        context.addDiscount(promotion.getName(), "PERCENTAGE_DISCOUNT", discountAmount);
    }
}

