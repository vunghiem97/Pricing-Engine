package com.example.pricingengine.controller;

import com.example.pricingengine.domain.entity.Coupon;
import com.example.pricingengine.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponController couponController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();
    }

    private Coupon buildCoupon(Long id, String code, BigDecimal amount, boolean active) {
        return Coupon.builder().id(id).code(code).discountAmount(amount).active(active).build();
    }

    @Test
    void getAll_returnsCoupons() throws Exception {
        Coupon c1 = buildCoupon(1L, "SAVE10", new BigDecimal("10.00"), true);
        Coupon c2 = buildCoupon(2L, "SAVE20", new BigDecimal("20.00"), false);
        when(couponRepository.findAll()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("SAVE10"))
                .andExpect(jsonPath("$[1].code").value("SAVE20"));
    }

    @Test
    void getActive_returnsOnlyActiveCoupons() throws Exception {
        Coupon c1 = buildCoupon(1L, "SAVE10", new BigDecimal("10.00"), true);
        when(couponRepository.findByActiveTrue()).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/coupons/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("SAVE10"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void getById_found_returns200() throws Exception {
        Coupon coupon = buildCoupon(1L, "SAVE10", new BigDecimal("10.00"), true);
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        mockMvc.perform(get("/api/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.discountAmount").value(10.00));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(couponRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/coupons/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByCode_found_returns200() throws Exception {
        Coupon coupon = buildCoupon(1L, "SAVE10", new BigDecimal("10.00"), true);
        when(couponRepository.findByCodeAndActiveTrue("SAVE10")).thenReturn(Optional.of(coupon));

        mockMvc.perform(get("/api/coupons/code/SAVE10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SAVE10"));
    }

    @Test
    void getByCode_notFound_returns404() throws Exception {
        when(couponRepository.findByCodeAndActiveTrue("BADCODE")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/coupons/code/BADCODE"))
                .andExpect(status().isNotFound());
    }
}
