package com.benecia.product_service.dto;

public record StockFailed(
        Long orderId,
        String userID,
        String reason
) {
}
