package com.benecia.product_service.service;

import com.benecia.product_service.common.AppException;
import com.benecia.product_service.common.ErrorCode;
import com.benecia.product_service.dto.ProductResponse;
import com.benecia.product_service.repository.ProductEntity;
import com.benecia.product_service.repository.ProductJpaRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductReader {

    private  final ProductJpaRepository productJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProductResponse getProduct(String productId) {
        String cacheKey = "product::" + productId;

        try {
            ProductEntity cachedProduct = (ProductEntity) redisTemplate.opsForValue().get(cacheKey);
            if (cachedProduct != null) {
                log.info("🎯 Cache Hit! (Redis) - {}", productId);
                return ProductResponse.from(cachedProduct);
            }
        } catch (Exception e) {
            log.warn("Redis connection failed, falling back to DB: {}", e.getMessage());
            // Redis가 죽어도 서비스는 돌아가야 하므로 로그만 찍고 DB로 넘어감
        }

        log.info("🐢 Cache Miss! (DB) - {}", productId);
        ProductEntity product = productJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        try {
            redisTemplate.opsForValue().set(cacheKey, product, Duration.ofMinutes(10));
        } catch (Exception e) {
            log.warn("Failed to cache product: {}", e.getMessage());
        }

        return ProductResponse.from(product);
    }
}
