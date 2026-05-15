package com.example.pricingengine.service;

import com.example.pricingengine.dto.request.OrderRequest;
import com.example.pricingengine.dto.response.PricingResponse;

public interface PricingService {

    PricingResponse calculate(OrderRequest request);
}

