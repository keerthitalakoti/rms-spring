package org.zeta.RestaurantManagement.servicetest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeta.RestaurantManagement.entity.TableBooking;
import org.zeta.RestaurantManagement.exception.BadRequestException;
import org.zeta.RestaurantManagement.exception.ResourceNotFoundException;
import org.zeta.RestaurantManagement.repository.TableBookingRepository;
import org.zeta.RestaurantManagement.service.TableBookingService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class TableBookingServiceTest {

    @Mock
    TableBookingRepository bookingRepo;

    @InjectMocks
    TableBookingService bookingSvc;

    @Test
    void createBookingSuccess() {
        TableBooking booking = new TableBooking(null, "Alice", LocalDateTime.now().plusHours(1), 3, 2);
        when(bookingRepo.save(booking)).thenReturn(booking);

        TableBooking created = bookingSvc.createBooking(booking);

        assertNotNull(created);
        assertEquals("Alice", created.getCustomerName());
        verify(bookingRepo).save(booking);
    }

    @Test
    void createBookingPastTime() {
        TableBooking booking = new TableBooking(null, "Alice", LocalDateTime.now().minusHours(1), 3, 2);
        assertThrows(BadRequestException.class, () -> bookingSvc.createBooking(booking));
        verify(bookingRepo, never()).save(any());
    }

    @Test
    void getBookingByIdFound() {
        TableBooking booking = new TableBooking(1L, "Alice", LocalDateTime.now().plusHours(1), 3, 2);
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        TableBooking found = bookingSvc.getBooking(1L);

        assertEquals(booking, found);
        verify(bookingRepo).findById(1L);
    }

    @Test
    void getBookingByIdNotFound() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingSvc.getBooking(1L));
        verify(bookingRepo).findById(1L);
    }

    @Test
    void updateBookingSuccess() {
        Long id = 1L;
        TableBooking update = new TableBooking(null, "Alice", LocalDateTime.now().plusDays(2), 4, 3);
        when(bookingRepo.existsById(id)).thenReturn(true);
        when(bookingRepo.save(any(TableBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TableBooking updated = bookingSvc.updateBooking(id, update);

        assertNotNull(updated);
        assertEquals(id, updated.getId());
        assertEquals("Alice", updated.getCustomerName());
        assertEquals(4, updated.getTableNumber());
        assertEquals(3, updated.getNumberOfGuests());
        assertEquals(update.getBookingTime(), updated.getBookingTime());
        verify(bookingRepo).save(any(TableBooking.class));
    }

    @Test
    void updateBookingBookingNotFound() {
        Long id = 1L;
        TableBooking update = new TableBooking(null, "Alice", LocalDateTime.now().plusDays(2), 4, 3);
        when(bookingRepo.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bookingSvc.updateBooking(id, update));
        verify(bookingRepo, never()).save(any());
    }

    @Test
    void updateBookingPastBookingTimeThrows() {
        Long id = 1L;
        TableBooking update = new TableBooking(null, "Alice", LocalDateTime.now().minusDays(1), 4, 3);
        when(bookingRepo.existsById(id)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> bookingSvc.updateBooking(id, update));
        assertEquals("Booking time must be now or in the future", ex.getMessage());
        verify(bookingRepo, never()).save(any());
    }

    @Test
    void deleteBookingSuccess() {
        Long id = 1L;
        when(bookingRepo.existsById(id)).thenReturn(true);
        doNothing().when(bookingRepo).deleteById(id);

        assertDoesNotThrow(() -> bookingSvc.deleteBooking(id));
        verify(bookingRepo).deleteById(id);
    }

    @Test
    void deleteBookingNotFound() {
        Long id = 1L;
        when(bookingRepo.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bookingSvc.deleteBooking(id));
        verify(bookingRepo, never()).deleteById(any());
    }
}
