package org.zeta.RestaurantManagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeta.RestaurantManagement.entity.Order;
import org.zeta.RestaurantManagement.exception.BadRequestException;
import org.zeta.RestaurantManagement.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;
import org.zeta.RestaurantManagement.repository.OrderRepository;

import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepo;

    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Order createOrder(Order order) {
        logger.info("Creating order for table {}", order.getTableNumber());

        if (order.getItems() == null || order.getItems().isEmpty()) {
            logger.warn("Order creation failed: items list is empty");
            throw new BadRequestException("Order must contain at least one item");
        }

        order.setStatus(Order.Status.PLACED);
        Order savedOrder = orderRepo.save(order);

        logger.info("Order created successfully with id {}", savedOrder.getId());
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderRepo.findAll();
    }

    public Order getOrder(Long id) {
        logger.info("Fetching order with id {}", id);
        return orderRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found with id {}", id);
                    return new OrderNotFoundException("Order not found with id: " + id);
                });
    }

    public Order updateOrderStatus(Long id, Order.Status status) {
        logger.info("Updating order status for order id {} to {}", id, status);

        if (status == null) {
            logger.error("Failed to update order id {}: status is null", id);
            throw new BadRequestException("Order status must be provided");
        }

        Order updatedOrder = orderRepo.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepo.save(order);
                })
                .orElseThrow(() -> {
                    logger.error("Order not found with id {}", id);
                    return new OrderNotFoundException("Order not found with id: " + id);
                });

        logger.info("Order id {} status updated to {}", id, status);
        return updatedOrder;
    }
}
