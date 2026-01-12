package com.benecia.product_service.service;

import com.benecia.product_service.dto.ProductResponse;
import com.benecia.product_service.dto.RegisterProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRegister productRegister;
    private final ProductReader productReader;

    public ProductResponse registerProduct(RegisterProduct request) {
        return productRegister.register(request);
    }

    public ProductResponse getProduct(String productId) {
        return productReader.getProduct(productId);
    }
}
