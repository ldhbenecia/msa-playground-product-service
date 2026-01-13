package com.benecia.product_service.service;

import com.benecia.product_service.event.StockFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final StreamBridge streamBridge;

    public void publishStockFailed(StockFailed failedDto) {
        try {
            log.error("📢 Publishing 'stock-failed' event for order: {}", failedDto.orderId());
            streamBridge.send("stockFailed-out-0", failedDto);
        } catch (Exception e) {
            log.error("Failed to publish stock-failed event", e);
        }
    }
}
