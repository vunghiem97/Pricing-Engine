package com.example.pricingengine.promotion.handler;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.promotion.AbstractPromotionHandler;
import com.example.pricingengine.promotion.PromotionContext;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy: Applies an extra percentage discount exclusively for VIP customers
 */
public class VipDiscountHandler extends AbstractPromotionHandler {

    private final Promotion promotion;

    public VipDiscountHandler(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    protected void applyPromotion(PromotionContext context) {
        if (context.getCustomerType() != CustomerType.VIP) {
            return; // Not applicable
        }
        BigDecimal percentage = promotion.getValue();
        BigDecimal discountAmount = context.getSubtotal()
                .multiply(percentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        context.addDiscount(promotion.getName(), "VIP_DISCOUNT", discountAmount);
    }
}

