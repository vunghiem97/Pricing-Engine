package com.example.pricingengine.promotion;

/**
 * Chain of Responsibility: each handler can apply a promotion and pass to the next.
 */
public interface PromotionHandler {

    /**
     * Set the next handler in the chain.
     * @return the next handler (for fluent chaining)
     */
    PromotionHandler setNext(PromotionHandler next);

    /**
     * Apply this promotion to the context and pass to the next handler.
     */
    void handle(PromotionContext context);
}

