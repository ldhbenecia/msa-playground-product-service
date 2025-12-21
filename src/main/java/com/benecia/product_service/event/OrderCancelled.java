package com.benecia.product_service.event;

public record OrderCancelled(
        Long orderId,
        String productId,
        Integer qty,
        String userId,
        Integer totalPrice
) {
}
