package com.benecia.product_service.controller;

import com.benecia.product_service.dto.RegisterProduct;
import com.benecia.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록 (초기 재고 세팅용)
    @PostMapping
    public void createProduct(@RequestBody RegisterProduct request) {
        productService.registerProduct(request);
    }

}
