package com.benecia.product_service.service;

import com.benecia.product_service.dto.ProductResponse;
import com.benecia.product_service.dto.RegisterProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductWriter productWriter;
    private final ProductReader productReader;

    @Transactional
    public ProductResponse registerProduct(RegisterProduct request) {
        return productWriter.register(request);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(String productId) {
        return productReader.getProduct(productId);
    }

    @Transactional
    public void decreaseStock(String productId, int qty) {
        productWriter.decreaseStock(productId, qty);
    }

    @Transactional
    public void increaseStock(String productId, int qty) {
        productWriter.increaseStock(productId, qty);
    }
}
