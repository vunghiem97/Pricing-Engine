package com.example.pricingengine.controller;

import com.example.pricingengine.domain.entity.Product;
import com.example.pricingengine.repository.ProductRepository;
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
class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    private Product buildProduct(Long id, String sku, String name, BigDecimal price, Integer stock) {
        return Product.builder().id(id).sku(sku).name(name).price(price).stock(stock).build();
    }

    @Test
    void getAll_returnsAllProducts() throws Exception {
        Product p1 = buildProduct(1L, "SKU-001", "Widget A", new BigDecimal("19.99"), 100);
        Product p2 = buildProduct(2L, "SKU-002", "Widget B", new BigDecimal("49.99"), 50);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sku").value("SKU-001"))
                .andExpect(jsonPath("$[1].sku").value("SKU-002"));
    }

    @Test
    void getById_found_returns200() throws Exception {
        Product product = buildProduct(1L, "SKU-001", "Widget A", new BigDecimal("19.99"), 100);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-001"))
                .andExpect(jsonPath("$.name").value("Widget A"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBySku_found_returns200() throws Exception {
        Product product = buildProduct(1L, "SKU-001", "Widget A", new BigDecimal("19.99"), 100);
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/sku/SKU-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-001"))
                .andExpect(jsonPath("$.stock").value(100));
    }

    @Test
    void getBySku_notFound_returns404() throws Exception {
        when(productRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/UNKNOWN"))
                .andExpect(status().isNotFound());
    }
}
