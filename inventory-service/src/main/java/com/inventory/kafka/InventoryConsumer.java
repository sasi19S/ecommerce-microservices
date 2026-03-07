package com.inventory.kafka;

import com.inventory.dto.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    @KafkaListener(topics = "order-topic-v2", groupId = "inventory-group")
    public void consume(Order order) {

        System.out.println("📦 Received Order: " + order);

        // 🔥 Business logic
        if (order.getQuantity() <= 5) {
            System.out.println("✅ Stock available for order: " + order.getId());
        } else {
            System.out.println("❌ Stock NOT available for order: " + order.getId());
        }
    }
}