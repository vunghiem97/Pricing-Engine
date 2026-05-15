package com.example.pricingengine.domain.entity;

import com.example.pricingengine.domain.enums.PromotionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "promotions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType type;

    /** For PERCENTAGE_DISCOUNT/VIP_DISCOUNT: percentage value (e.g. 10 = 10%).
     *  For BUY_X_GET_Y: buyQuantity (e.g. 2 means buy 2 get 1). */
    @Column(name = "promo_value", precision = 12, scale = 2)
    private BigDecimal value;

    /** For BUY_X_GET_Y: free quantity per cycle. */
    private Integer freeQuantity;

    @Column(nullable = false)
    private boolean active;
}


