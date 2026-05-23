package com.example.pricingengine.promotion.handler;

import com.example.pricingengine.domain.entity.Coupon;
import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.promotion.PromotionContext;
import com.example.pricingengine.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponDiscountHandlerTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponDiscountHandler handler;

    private PromotionContext buildContext(String couponCode, String subtotal) {
        OrderItemRequest item = OrderItemRequest.builder()
                .sku("SKU-001").price(new BigDecimal(subtotal)).quantity(1).build();
        return new PromotionContext(CustomerType.NORMAL, List.of(item), couponCode, new BigDecimal(subtotal));
    }

    @Test
    void applyPromotion_validCoupon_appliesDiscount() {
        Coupon coupon = Coupon.builder()
                .code("SAVE20").discountAmount(new BigDecimal("20.00")).active(true).build();
        when(couponRepository.findByCodeAndActiveTrue("SAVE20")).thenReturn(Optional.of(coupon));

        PromotionContext context = buildContext("SAVE20", "100.00");
        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("20.00");
        assertThat(context.getBreakdowns()).hasSize(1);
        assertThat(context.getBreakdowns().get(0).getPromotionType()).isEqualTo("COUPON");
        assertThat(context.getBreakdowns().get(0).getPromotionName()).isEqualTo("Coupon: SAVE20");
    }

    @Test
    void applyPromotion_couponCodeTrimmedAndUppercased() {
        Coupon coupon = Coupon.builder()
                .code("SAVE20").discountAmount(new BigDecimal("20.00")).active(true).build();
        when(couponRepository.findByCodeAndActiveTrue("SAVE20")).thenReturn(Optional.of(coupon));

        PromotionContext context = buildContext("  save20  ", "100.00");
        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo("20.00");
        verify(couponRepository).findByCodeAndActiveTrue("SAVE20");
    }

    @Test
    void applyPromotion_nullCouponCode_noDiscount() {
        PromotionContext context = buildContext(null, "100.00");
        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
        verifyNoInteractions(couponRepository);
    }

    @Test
    void applyPromotion_emptyCouponCode_noDiscount() {
        PromotionContext context = buildContext("   ", "100.00");
        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
        verifyNoInteractions(couponRepository);
    }

    @Test
    void applyPromotion_unknownCoupon_noDiscount() {
        when(couponRepository.findByCodeAndActiveTrue("INVALID")).thenReturn(Optional.empty());

        PromotionContext context = buildContext("INVALID", "100.00");
        handler.handle(context);

        assertThat(context.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getBreakdowns()).isEmpty();
    }
}

