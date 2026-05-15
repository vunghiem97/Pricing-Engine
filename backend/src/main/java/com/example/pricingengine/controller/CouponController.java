package com.example.pricingengine.controller;

import com.example.pricingengine.domain.entity.Coupon;
import com.example.pricingengine.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;

    @GetMapping
    public ResponseEntity<List<Coupon>> getAll() {
        return ResponseEntity.ok(couponRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Coupon>> getActive() {
        return ResponseEntity.ok(couponRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getById(@PathVariable Long id) {
        return couponRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Coupon> getById(@PathVariable String code) {
        return couponRepository.findByCodeAndActiveTrue(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

