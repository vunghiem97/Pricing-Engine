package com.example.pricingengine.repository;

import com.example.pricingengine.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeAndActiveTrue(String code);
    List<Coupon> findByActiveTrue();
}

