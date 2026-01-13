package com.benecia.product_service.service;

import com.benecia.product_service.common.AppException;
import com.benecia.product_service.common.ErrorCode;
import com.benecia.product_service.dto.ProductResponse;
import com.benecia.product_service.dto.RegisterProduct;
import com.benecia.product_service.repository.ProductEntity;
import com.benecia.product_service.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductWriter {

    private final ProductJpaRepository productJpaRepository;

    public ProductResponse register(RegisterProduct request) {
        ProductEntity product = new ProductEntity(
                request.productId(),
                request.name(),
                request.stock(),
                request.unitPrice()
        );

        productJpaRepository.save(product);
        return ProductResponse.from(product);
    }

    @Transactional
    public void decreaseStock(String productId, int qty) {
        // DLQ 테스트용 폭탄 로직
        if ("BOMB".equals(productId)) {
            log.error("💣 Product Service: 으악! 폭탄이다! (DLQ 테스트)");
            throw new RuntimeException("Product Service Error Triggered!");
        }

        ProductEntity product = productJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + productId));

        product.decreaseStock(qty);

        log.info("Stock decreased for {}. Remaining: {}", productId, product.getStock());
    }

    @Transactional
    public void increaseStock(String productId, int qty) {
        ProductEntity product = productJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + productId));

        product.increaseStock(qty);

        log.info("Stock increased for {}. Remaining: {}", productId, product.getStock());
    }
}
