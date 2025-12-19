package com.benecia.product_service.service;

import com.benecia.product_service.dto.RegisterProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRegister productRegister;

    public void registerProduct(RegisterProduct request) {
        productRegister.register(request);
    }

    public void decreaseStock(String productId, int qty) {
        productRegister.decreaseStock(productId, qty);
    }
}
