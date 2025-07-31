package org.zeta.RestaurantManagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zeta.RestaurantManagement.entity.TableBooking;
import org.zeta.RestaurantManagement.exception.BadRequestException;
import org.zeta.RestaurantManagement.exception.ResourceNotFoundException;
import org.zeta.RestaurantManagement.repository.TableBookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TableBookingService {

    private static final Logger logger = LoggerFactory.getLogger(TableBookingService.class);

    private final TableBookingRepository bookingRepo;

    public TableBookingService(TableBookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    public TableBooking createBooking(TableBooking booking) {
        logger.info("Creating booking for customer {} at time {}", booking.getCustomerName(), booking.getBookingTime());

        if (booking.getBookingTime() == null || booking.getBookingTime().isBefore(LocalDateTime.now())) {
            logger.warn("Booking creation failed: booking time invalid {}", booking.getBookingTime());
            throw new BadRequestException("Booking time must be a future or present date");
        }

        TableBooking saved = bookingRepo.save(booking);
        logger.info("Booking created with id {}", saved.getId());
        return saved;
    }

    public List<TableBooking> getAllBookings() {
        logger.info("Fetching all bookings");
        return bookingRepo.findAll();
    }

    public TableBooking getBooking(Long id) {
        logger.info("Fetching booking with id {}", id);

        return bookingRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Booking not found with id {}", id);
                    return new ResourceNotFoundException("Booking not found with id: " + id);
                });
    }

    public TableBooking updateBooking(Long id, TableBooking booking) {
        logger.info("Updating booking with id {}", id);

        if (!bookingRepo.existsById(id)) {
            logger.error("Failed to update booking: not found with id {}", id);
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }

        booking.setId(id);

        if (booking.getBookingTime() != null && booking.getBookingTime().isBefore(LocalDateTime.now())) {
            logger.warn("Booking update failed due to past booking time {}", booking.getBookingTime());
            throw new BadRequestException("Booking time must be now or in the future");
        }

        TableBooking updated = bookingRepo.save(booking);

        logger.info("Booking with id {} updated successfully", id);
        return updated;
    }

    public void deleteBooking(Long id) {
        logger.info("Deleting booking with id {}", id);

        if (!bookingRepo.existsById(id)) {
            logger.error("Failed to delete booking: not found with id {}", id);
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }

        bookingRepo.deleteById(id);
        logger.info("Booking with id {} deleted", id);
    }
}
