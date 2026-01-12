package com.benecia.product_service.service;

import com.benecia.product_service.common.AppException;
import com.benecia.product_service.event.OrderCancelled;
import com.benecia.product_service.event.OrderCreated;
import com.benecia.product_service.event.StockFailed;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRegister productRegister;
    private final StreamBridge streamBridge;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public Consumer<OrderCreated> orderCreated() {
        return orderDto -> {
            log.info("Received order-created event: {}", orderDto);

            try {
                productRegister.decreaseStock(orderDto.productId(), orderDto.qty());
                log.info("Stock decreased successfully for orderId: {}", orderDto.orderId());

                // Redis 캐시 삭제 (Cache Eviction)
                // 재고가 바뀌었으니, Redis에 저장된 옛날 정보("product::CAT-001")를 지움
                // 그래야 다음 조회 때 DB에서 최신 재고(99개)를 새로 가져와서 캐싱함
                String cacheKey = "product::" + orderDto.productId();
                redisTemplate.delete(cacheKey);
                log.info("🧹 Cache Evicted for: {}", cacheKey);
            } catch(AppException e) {
                log.error("Failed to decrease stock: {}", e.getMessage());
                StockFailed failedDto = new StockFailed(orderDto.orderId(), orderDto.userId(), e.getMessage());
                streamBridge.send("stockFailed-out-0", failedDto);
            }
        };
    }

    @Bean
    public Consumer<OrderCancelled> orderCancelled() {
        return cancelledDto -> {
            log.info("Received order-cancelled. Restoring stock for productId: {}", cancelledDto.productId());
            try {
                productRegister.increaseStock(cancelledDto.productId(), cancelledDto.qty());

                String cacheKey = "product::" + cancelledDto.productId();
                redisTemplate.delete(cacheKey);
                log.info("🧹 Cache Evicted (Restored) for: {}", cacheKey);
            } catch (Exception e) {
                // 이미 롤백됐거나 상품이 없는 경우 등. 로그만 남김.
                log.warn("Failed to restore stock (might be already handled): {}", e.getMessage());
            }
        };
    }
}
