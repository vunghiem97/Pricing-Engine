package com.example.pricingengine.promotion.handler;

import com.example.pricingengine.domain.entity.Coupon;
import com.example.pricingengine.promotion.AbstractPromotionHandler;
import com.example.pricingengine.promotion.PromotionContext;
import com.example.pricingengine.repository.CouponRepository;
import java.util.Optional;

/**
 * Strategy: Applies a fixed dollar amount discount from a coupon code
 */
public class CouponDiscountHandler extends AbstractPromotionHandler {

    private final CouponRepository couponRepository;

    public CouponDiscountHandler(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    protected void applyPromotion(PromotionContext context) {
        String code = context.getCouponCode();
        if (code == null || code.isBlank()) {
            return;
        }
        Optional<Coupon> couponOpt = couponRepository.findByCodeAndActiveTrue(code.trim().toUpperCase());
        couponOpt.ifPresent(coupon ->
                context.addDiscount("Coupon: " + coupon.getCode(), "COUPON", coupon.getDiscountAmount())
        );
    }
}

