package com.example.pricingengine.config;

import com.example.pricingengine.domain.entity.Coupon;
import com.example.pricingengine.domain.entity.Product;
import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.domain.enums.PromotionType;
import com.example.pricingengine.repository.CouponRepository;
import com.example.pricingengine.repository.ProductRepository;
import com.example.pricingengine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) {
        seedProducts();
        seedPromotions();
        seedCoupons();
        log.info("Seed data loaded successfully");
    }

    private void seedProducts() {
        productRepository.save(Product.builder().sku("A100").name("Card 100").price(new BigDecimal("100.00")).stock(10).build());
        productRepository.save(Product.builder().sku("B200").name("Card 50").price(new BigDecimal("50.00")).stock(20).build());
    }

    private void seedPromotions() {
        // 10% off for everyone
        promotionRepository.save(Promotion.builder()
                .name("10% Off Order Total")
                .type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(new BigDecimal("10"))
                .active(true)
                .build());

        // Buy 2 Get 1 Free
        promotionRepository.save(Promotion.builder()
                .name("Buy 2 Get 1 Free")
                .type(PromotionType.BUY_X_GET_Y)
                .value(new BigDecimal("2"))  // buy 2
                .freeQuantity(1)              // get 1 free
                .active(true)
                .build());

        // VIP 5% extra discount
        promotionRepository.save(Promotion.builder()
                .name("VIP 5% Extra Discount")
                .type(PromotionType.VIP_DISCOUNT)
                .value(new BigDecimal("5"))
                .active(true)
                .build());
    }

    private void seedCoupons() {
        couponRepository.save(Coupon.builder()
                .code("SUMMER10")
                .discountAmount(new BigDecimal("10.00"))
                .active(true)
                .build());

        couponRepository.save(Coupon.builder()
                .code("SAVE20")
                .discountAmount(new BigDecimal("20.00"))
                .active(true)
                .build());

        couponRepository.save(Coupon.builder()
                .code("WELCOME5")
                .discountAmount(new BigDecimal("5.00"))
                .active(true)
                .build());
    }
}

