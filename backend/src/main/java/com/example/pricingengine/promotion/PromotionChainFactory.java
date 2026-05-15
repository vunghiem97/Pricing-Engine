package com.example.pricingengine.promotion;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.promotion.handler.*;
import com.example.pricingengine.repository.CouponRepository;
import com.example.pricingengine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionChainFactory {

    private final PromotionRepository promotionRepository;
    private final CouponRepository couponRepository;

    public PromotionHandler buildChain() {
        List<Promotion> activePromotions = promotionRepository.findByActiveTrue();

        // Head of chain - default handler (current nothing apply)
        PromotionHandler head = new DefaultPromotionHandler();
        PromotionHandler current = head;

        for (Promotion promotion : activePromotions) {
            PromotionHandler handler = createHandler(promotion);
            if (handler != null) {
                current.setNext(handler);
                current = handler;
            }
        }

        //coupon handler always added last (reads from coupon repo)
        current.setNext(new CouponDiscountHandler(couponRepository));

        return head;
    }

    private PromotionHandler createHandler(Promotion promotion) {
        return switch (promotion.getType()) {
            case PERCENTAGE_DISCOUNT -> new PercentageDiscountHandler(promotion);
            case BUY_X_GET_Y         -> new BuyXGetYHandler(promotion);
            case VIP_DISCOUNT        -> new VipDiscountHandler(promotion);
            case COUPON              -> null; //handled separately via CouponDiscountHandler
        };
    }

    private static class DefaultPromotionHandler extends AbstractPromotionHandler {
        @Override
        protected void applyPromotion(PromotionContext context) {
            // nothing
        }
    }
}

