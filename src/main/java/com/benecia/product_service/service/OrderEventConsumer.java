package com.benecia.product_service.service;

import com.benecia.product_service.common.AppException;
import com.benecia.product_service.dto.OrderCreated;
import com.benecia.product_service.dto.StockFailed;
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

                StockFailed failedDto = new StockFailed(
                        orderDto.orderId(),
                        orderDto.userId(),
                        e.getMessage()
                );

                streamBridge.send("stockFailed-out-0", failedDto);
                log.info("Published stock-failed event for orderId: {}", orderDto.orderId());
            }
        };
    }
}
