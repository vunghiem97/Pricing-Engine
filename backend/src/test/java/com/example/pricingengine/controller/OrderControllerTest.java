package com.example.pricingengine.controller;

import com.example.pricingengine.domain.enums.CustomerType;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.dto.request.OrderRequest;
import com.example.pricingengine.dto.response.PricingResponse;
import com.example.pricingengine.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void calculate_validRequest_returns200WithResponse() throws Exception {
        PricingResponse mockResponse = PricingResponse.builder()
                .subtotal(new BigDecimal("200.00"))
                .discount(new BigDecimal("20.00"))
                .finalPrice(new BigDecimal("180.00"))
                .discountBreakdown(List.of(
                        PricingResponse.DiscountBreakdown.builder()
                                .promotionName("10% Off")
                                .promotionType("PERCENTAGE_DISCOUNT")
                                .discountAmount(new BigDecimal("20.00"))
                                .build()
                ))
                .build();
        when(pricingService.calculate(any())).thenReturn(mockResponse);

        OrderRequest request = OrderRequest.builder()
                .customerType(CustomerType.NORMAL)
                .items(List.of(OrderItemRequest.builder()
                        .sku("SKU-001").price(new BigDecimal("100.00")).quantity(2).build()))
                .build();

        mockMvc.perform(post("/api/orders/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(200.00))
                .andExpect(jsonPath("$.discount").value(20.00))
                .andExpect(jsonPath("$.finalPrice").value(180.00))
                .andExpect(jsonPath("$.discountBreakdown[0].promotionName").value("10% Off"))
                .andExpect(jsonPath("$.discountBreakdown[0].promotionType").value("PERCENTAGE_DISCOUNT"));
    }

    @Test
    void calculate_noDiscount_returns200WithEmptyBreakdown() throws Exception {
        PricingResponse mockResponse = PricingResponse.builder()
                .subtotal(new BigDecimal("100.00"))
                .discount(BigDecimal.ZERO)
                .finalPrice(new BigDecimal("100.00"))
                .discountBreakdown(List.of())
                .build();
        when(pricingService.calculate(any())).thenReturn(mockResponse);

        OrderRequest request = OrderRequest.builder()
                .customerType(CustomerType.NORMAL)
                .items(List.of(OrderItemRequest.builder()
                        .sku("SKU-001").price(new BigDecimal("100.00")).quantity(1).build()))
                .build();

        mockMvc.perform(post("/api/orders/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountBreakdown").isEmpty());
    }

    @Test
    void calculate_vipCustomer_returns200() throws Exception {
        PricingResponse mockResponse = PricingResponse.builder()
                .subtotal(new BigDecimal("100.00"))
                .discount(new BigDecimal("5.00"))
                .finalPrice(new BigDecimal("95.00"))
                .discountBreakdown(List.of())
                .build();
        when(pricingService.calculate(any())).thenReturn(mockResponse);

        OrderRequest request = OrderRequest.builder()
                .customerType(CustomerType.VIP)
                .items(List.of(OrderItemRequest.builder()
                        .sku("VIP-SKU").price(new BigDecimal("100.00")).quantity(1).build()))
                .couponCode("VIPCOUPON")
                .build();

        mockMvc.perform(post("/api/orders/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalPrice").value(95.00));
    }
}
