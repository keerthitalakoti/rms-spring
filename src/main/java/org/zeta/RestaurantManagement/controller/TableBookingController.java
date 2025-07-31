package org.zeta.RestaurantManagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeta.RestaurantManagement.entity.TableBooking;
import org.zeta.RestaurantManagement.service.TableBookingService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class TableBookingController {

    private static final Logger logger = LoggerFactory.getLogger(TableBookingController.class);

    private final TableBookingService bookingService;

    public TableBookingController(TableBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<TableBooking> createBooking(@Valid @RequestBody TableBooking booking) {
        logger.info("Received request to create booking for customer {}", booking.getCustomerName());
        TableBooking created = bookingService.createBooking(booking);
        logger.info("Booking created with id {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<TableBooking> getAllBookings() {
        logger.info("Received request to get all bookings");
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableBooking> getBooking(@PathVariable Long id) {
        logger.info("Received request to get booking with id {}", id);
        TableBooking booking = bookingService.getBooking(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableBooking> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody TableBooking booking
    ) {
        logger.info("Received request to update booking with id {}", id);
        TableBooking updated = bookingService.updateBooking(id, booking);
        logger.info("Booking with id {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        logger.info("Received request to delete booking with id {}", id);
        bookingService.deleteBooking(id);
        logger.info("Booking with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
