package com.order.service;

import com.order.entity.Order;
import com.order.kafka.OrderProducer;
import com.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repo;
    private final OrderProducer producer;

    public OrderService(OrderRepository repo, OrderProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    public Order create(Order order) {
        order.setStatus("CREATED");

        Order saved = repo.save(order);

        // 🔥 Publish event
        producer.sendOrder(saved);

        return saved;
    }

    // ✅ ADD THIS METHOD
    public List<Order> getAll() {
        return repo.findAll();
    }
}