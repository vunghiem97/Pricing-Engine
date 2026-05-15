package com.example.pricingengine.promotion;

public abstract class AbstractPromotionHandler implements PromotionHandler {

    private PromotionHandler next;

    @Override
    public PromotionHandler setNext(PromotionHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public final void handle(PromotionContext context) {
        applyPromotion(context);
        if (next != null) {
            next.handle(context);
        }
    }

    protected abstract void applyPromotion(PromotionContext context);
}

