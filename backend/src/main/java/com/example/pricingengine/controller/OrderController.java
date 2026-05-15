package com.example.pricingengine.controller;

import com.example.pricingengine.dto.request.OrderRequest;
import com.example.pricingengine.dto.response.PricingResponse;
import com.example.pricingengine.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PricingService pricingService;

    @PostMapping("/calculate")
    public ResponseEntity<PricingResponse> calculate(@Valid @RequestBody OrderRequest request) {
        PricingResponse response = pricingService.calculate(request);
        return ResponseEntity.ok(response);
    }
}

