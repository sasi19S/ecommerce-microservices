package com.order.controller;

import com.order.entity.Order;
import com.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        return service.create(order);
    }

    @GetMapping
    public List<Order> getAll() {
        return service.getAll();
    }
}