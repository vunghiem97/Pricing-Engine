package com.example.pricingengine.promotion.handler;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.promotion.AbstractPromotionHandler;
import com.example.pricingengine.promotion.PromotionContext;
import java.math.BigDecimal;

/**
 * Strategy: Buy X get Y free
 */
public class BuyXGetYHandler extends AbstractPromotionHandler {

    private final Promotion promotion;

    public BuyXGetYHandler(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    protected void applyPromotion(PromotionContext context) {
        int buyQty = promotion.getValue().intValue();
        int freeQty = promotion.getFreeQuantity();
        int cycleSize = buyQty + freeQty;

        BigDecimal totalFreeValue = BigDecimal.ZERO;

        for (OrderItemRequest item : context.getItems()) {
            int qty = item.getQuantity();
            if (qty >= cycleSize) {
                int cycles = qty / cycleSize;
                int freeUnits = cycles * freeQty;
                totalFreeValue = totalFreeValue.add(
                        item.getPrice().multiply(BigDecimal.valueOf(freeUnits))
                );
            }
        }

        context.addDiscount(promotion.getName(), "BUY_X_GET_Y", totalFreeValue);
    }
}

