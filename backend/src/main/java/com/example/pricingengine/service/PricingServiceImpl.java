package com.example.pricingengine.service;

import com.example.pricingengine.domain.entity.Order;
import com.example.pricingengine.domain.entity.OrderItem;
import com.example.pricingengine.dto.request.OrderItemRequest;
import com.example.pricingengine.dto.request.OrderRequest;
import com.example.pricingengine.dto.response.PricingResponse;
import com.example.pricingengine.promotion.PromotionChainFactory;
import com.example.pricingengine.promotion.PromotionContext;
import com.example.pricingengine.promotion.PromotionHandler;
import com.example.pricingengine.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

/**
 * Single Responsibility: orchestrates pricing calculation.
 * Delegates promotion logic to the chain; persistence to the repository.
 */
@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final PromotionChainFactory promotionChainFactory;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PricingResponse calculate(OrderRequest request) {

        //compute subtotal
        BigDecimal subtotal = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //build promotion context
        PromotionContext context = new PromotionContext(
                request.getCustomerType(),
                request.getItems(),
                request.getCouponCode(),
                subtotal
        );

        //run promotion chain
        PromotionHandler chain = promotionChainFactory.buildChain();
        chain.handle(context);

        BigDecimal totalDiscount = context.getTotalDiscount();
        //ensure discount never exceeds subtotal
        if (totalDiscount.compareTo(subtotal) > 0) {
            totalDiscount = subtotal;
        }
        BigDecimal finalPrice = subtotal.subtract(totalDiscount);

        //persist order
        Order order = buildOrder(request, subtotal, totalDiscount, finalPrice);
        orderRepository.save(order);

        return PricingResponse.builder()
                .subtotal(subtotal)
                .discount(totalDiscount)
                .finalPrice(finalPrice)
                .discountBreakdown(context.getBreakdowns())
                .build();
    }

    private Order buildOrder(OrderRequest request,
                              BigDecimal subtotal,
                              BigDecimal discount,
                              BigDecimal finalPrice) {
        Order order = Order.builder()
                .customerType(request.getCustomerType())
                .couponCode(request.getCouponCode())
                .subtotal(subtotal)
                .discount(discount)
                .finalPrice(finalPrice)
                .build();

        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .sku(itemReq.getSku())
                    .price(itemReq.getPrice())
                    .quantity(itemReq.getQuantity())
                    .build();
            order.getItems().add(item);
        }
        return order;
    }
}

