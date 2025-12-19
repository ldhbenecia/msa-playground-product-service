package com.benecia.product_service.dto;

public record RegisterProduct(
        String productId,
        String name,
        int stock,
        int unitPrice
) {
}
