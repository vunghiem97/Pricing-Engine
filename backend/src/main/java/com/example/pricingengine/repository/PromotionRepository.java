package com.example.pricingengine.repository;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.domain.enums.PromotionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByActiveTrue();
    List<Promotion> findByTypeAndActiveTrue(PromotionType type);
}

