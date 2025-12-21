package com.benecia.product_service.event;

public record PointsFailed(
        Long orderId,
        String userId,
        String reason
) {
}
