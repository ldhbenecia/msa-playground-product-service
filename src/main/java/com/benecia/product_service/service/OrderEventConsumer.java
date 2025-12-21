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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRegister productRegister;
    private final StreamBridge streamBridge;

    @Bean
    public Consumer<OrderCreated> orderCreated() {
        return orderDto -> {
            log.info("Received order-created event: {}", orderDto);

            try {
                productRegister.decreaseStock(orderDto.productId(), orderDto.qty());
                log.info("Stock decreased successfully for orderId: {}", orderDto.orderId());
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
            } catch (Exception e) {
                // 이미 롤백됐거나 상품이 없는 경우 등. 로그만 남김.
                log.warn("Failed to restore stock (might be already handled): {}", e.getMessage());
            }
        };
    }
}
