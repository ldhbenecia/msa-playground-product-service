package com.benecia.product_service.event;

public record StockFailed(
        Long orderId,
        String userID,
        String reason
) {
}
