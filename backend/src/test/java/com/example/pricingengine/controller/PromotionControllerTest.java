package com.example.pricingengine.controller;

import com.example.pricingengine.domain.entity.Promotion;
import com.example.pricingengine.domain.enums.PromotionType;
import com.example.pricingengine.repository.PromotionRepository;
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
class PromotionControllerTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionController promotionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(promotionController).build();
    }

    private Promotion buildPromotion(Long id, String name, PromotionType type, BigDecimal value, boolean active) {
        return Promotion.builder().id(id).name(name).type(type).value(value).active(active).build();
    }

    @Test
    void getAll_returnsAllPromotions() throws Exception {
        Promotion p1 = buildPromotion(1L, "10% Off", PromotionType.PERCENTAGE_DISCOUNT, new BigDecimal("10"), true);
        Promotion p2 = buildPromotion(2L, "VIP 5%", PromotionType.VIP_DISCOUNT, new BigDecimal("5"), false);
        when(promotionRepository.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("10% Off"))
                .andExpect(jsonPath("$[1].name").value("VIP 5%"));
    }

    @Test
    void getActive_returnsOnlyActivePromotions() throws Exception {
        Promotion p1 = buildPromotion(1L, "10% Off", PromotionType.PERCENTAGE_DISCOUNT, new BigDecimal("10"), true);
        when(promotionRepository.findByActiveTrue()).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/promotions/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void getById_found_returns200() throws Exception {
        Promotion p = buildPromotion(1L, "10% Off", PromotionType.PERCENTAGE_DISCOUNT, new BigDecimal("10"), true);
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/promotions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("10% Off"))
                .andExpect(jsonPath("$.type").value("PERCENTAGE_DISCOUNT"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(promotionRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/promotions/99"))
                .andExpect(status().isNotFound());
    }
}
