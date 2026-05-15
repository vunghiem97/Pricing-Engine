package com.example.pricingengine.promotion;

import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.dto.response.PricingResponse;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PromotionContext {

    private final CustomerType customerType;
    private final List<OrderItemRequest> items;
    private final String couponCode;
    private final BigDecimal subtotal;

    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private final List<PricingResponse.DiscountBreakdown> breakdowns = new ArrayList<>();

    public PromotionContext(CustomerType customerType,
                            List<OrderItemRequest> items,
                            String couponCode,
                            BigDecimal subtotal) {
        this.customerType = customerType;
        this.items = items;
        this.couponCode = couponCode;
        this.subtotal = subtotal;
    }

    public void addDiscount(String promotionName, String promotionType, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.totalDiscount = this.totalDiscount.add(amount);
            this.breakdowns.add(PricingResponse.DiscountBreakdown.builder()
                    .promotionName(promotionName)
                    .promotionType(promotionType)
                    .discountAmount(amount)
                    .build());
        }
    }

    public BigDecimal getRemainingAmount() {
        BigDecimal remaining = subtotal.subtract(totalDiscount);
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }
}

