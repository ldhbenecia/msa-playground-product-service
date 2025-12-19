package com.benecia.product_service.service;

import com.benecia.product_service.common.AppException;
import com.benecia.product_service.common.ErrorCode;
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
public class ProductRegister {

    private final ProductJpaRepository productJpaRepository;

    @Transactional
    public void register(RegisterProduct request) {
        ProductEntity productEntity = new ProductEntity(
                request.productId(),
                request.name(),
                request.stock(),
                request.unitPrice()
        );

        productJpaRepository.save(productEntity);
    }

    @Transactional
    public void decreaseStock(String productId, int qty) {
        ProductEntity product = productJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + productId));
        product.decreaseStock(qty);

        log.info("Stock decreased for {}. Remaining: {}", productId, product.getStock());
    }
}
