package org.zeta.RestaurantManagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeta.RestaurantManagement.entity.Order;
import org.zeta.RestaurantManagement.service.OrderService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        logger.info("Received request to create order for table {}", order.getTableNumber());
        Order created = orderService.createOrder(order);
        logger.info("Order created with id {}", created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        logger.info("Received request to get all orders");
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        logger.info("Received request to get order with id {}", id);
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Order order) {
        if (order.getStatus() == null) {
            logger.warn("Update order status request with missing status for order id {}", id);
            return ResponseEntity.badRequest().build();
        }
        logger.info("Received request to update order id {} status to {}", id, order.getStatus());
        Order updated = orderService.updateOrderStatus(id, order.getStatus());
        logger.info("Order id {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }
}
