package com.payment.kafka;

import com.payment.dto.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    @KafkaListener(topics = "order-topic-v2", groupId = "payment-group")
    public void processPayment(Order order) {

        System.out.println("💰 Processing payment for order: " + order.getId());

        // Simulated payment logic
        if (order.getQuantity() <= 5) {
            System.out.println("✅ Payment SUCCESS for order: " + order.getId());
        } else {
            System.out.println("❌ Payment FAILED for order: " + order.getId());
        }
    }
}