package com.example.pricingengine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingResponse {

    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    private List<DiscountBreakdown> discountBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountBreakdown {
        private String promotionName;
        private String promotionType;
        private BigDecimal discountAmount;
    }
}

