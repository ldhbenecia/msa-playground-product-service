package com.benecia.product_service.service;

import com.benecia.product_service.common.AppException;
import com.benecia.product_service.event.OrderCancelled;
import com.benecia.product_service.event.OrderCreated;
import com.benecia.product_service.event.StockFailed;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ProductService productService;
    private final ProductEventPublisher productEventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    private void evictCache(String productId) {
        String cacheKey = "product::" + productId;
        redisTemplate.delete(cacheKey);
        log.info("🧹 Cache Evicted for: {}", cacheKey);
    }

    @Bean
    public Consumer<OrderCreated> orderCreated() {
        return orderDto -> {
            log.info("📨 Received 'order-created': {}", orderDto.orderId());

            try {
                productService.decreaseStock(orderDto.productId(), orderDto.qty());
                evictCache(orderDto.productId());
            } catch(AppException e) {
                log.error("Failed to decrease stock: {}", e.getMessage());
                StockFailed failedDto = new StockFailed(orderDto.orderId(), orderDto.userId(), e.getMessage());
                productEventPublisher.publishStockFailed(failedDto);
            }
        };
    }

    @Bean
    public Consumer<OrderCancelled> orderCancelled() {
        return cancelledDto -> {
            log.info("📨 Received 'order-cancelled': {}", cancelledDto.orderId());
            try {
                productService.increaseStock(cancelledDto.productId(), cancelledDto.qty());
                evictCache(cancelledDto.productId());
            } catch (Exception e) {
                // 이미 롤백됐거나 상품이 없는 경우 등. 로그만 남김.
                log.warn("Failed to restore stock (might be already handled): {}", e.getMessage());
            }
        };
    }
}
