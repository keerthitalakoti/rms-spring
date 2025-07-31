package org.zeta.RestaurantManagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeta.RestaurantManagement.entity.Order;
public interface OrderRepository extends JpaRepository<Order, Long> {}

