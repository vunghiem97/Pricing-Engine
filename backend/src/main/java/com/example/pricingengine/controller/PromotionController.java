package com.example.pricingengine.controller;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionRepository promotionRepository;

    @GetMapping
    public ResponseEntity<List<Promotion>> getAll() {
        return ResponseEntity.ok(promotionRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActive() {
        return ResponseEntity.ok(promotionRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getById(@PathVariable Long id) {
        return promotionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

