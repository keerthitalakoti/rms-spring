package org.zeta.RestaurantManagement.repository;
import org.zeta.RestaurantManagement.entity.TableBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {}
