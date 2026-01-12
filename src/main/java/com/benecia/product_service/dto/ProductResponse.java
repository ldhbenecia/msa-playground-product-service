package com.benecia.product_service.dto;

import com.benecia.product_service.repository.ProductEntity;
import java.time.LocalDateTime;

public record ProductResponse(
        String productId,
        String name,
        int stock,
        int unitPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProductResponse from(ProductEntity entity) {
        return new ProductResponse(
                entity.getProductId(),
                entity.getName(),
                entity.getStock(),
                entity.getUnitPrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
