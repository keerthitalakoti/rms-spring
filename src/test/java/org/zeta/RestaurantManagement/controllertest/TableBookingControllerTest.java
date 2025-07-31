package org.zeta.RestaurantManagement.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zeta.RestaurantManagement.controller.TableBookingController;
import org.zeta.RestaurantManagement.entity.TableBooking;
import org.zeta.RestaurantManagement.exception.ResourceNotFoundException;
import org.zeta.RestaurantManagement.service.TableBookingService;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableBookingController.class)
public class TableBookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TableBookingService bookingSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createBooking_Success() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
        TableBooking booking = new TableBooking(1L, "Alice", futureTime, 3, 2);

        Mockito.when(bookingSvc.createBooking(any(TableBooking.class))).thenReturn(booking);

        String json = objectMapper.writeValueAsString(booking);

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.tableNumber").value(3))
                .andExpect(jsonPath("$.numberOfGuests").value(2));
    }

    @Test
    public void getAllBookings_ReturnsList() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        TableBooking b1 = new TableBooking(1L, "Alice", now.plusHours(1), 3, 2);
        TableBooking b2 = new TableBooking(2L, "Bob", now.plusHours(2), 4, 4);

        Mockito.when(bookingSvc.getAllBookings()).thenReturn(Arrays.asList(b1, b2));

        mvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("Alice"))
                .andExpect(jsonPath("$[1].customerName").value("Bob"));
    }

    @Test
    public void getBookingById_Success() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        TableBooking booking = new TableBooking(1L, "Alice", now.plusHours(1), 3, 2);

        Mockito.when(bookingSvc.getBooking(1L)).thenReturn(booking);

        mvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.tableNumber").value(3));
    }

    @Test
    public void getBookingById_NotFound() throws Exception {
        Mockito.when(bookingSvc.getBooking(1L)).thenThrow(new ResourceNotFoundException("Booking not found"));

        mvc.perform(get("/bookings/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    public void updateBooking_Success() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        TableBooking updateRequest = new TableBooking(null, "Alice Updated", futureTime, 5, 3);
        TableBooking updatedBooking = new TableBooking(1L, "Alice Updated", futureTime, 5, 3);

        Mockito.when(bookingSvc.updateBooking(eq(1L), any(TableBooking.class))).thenReturn(updatedBooking);

        String json = objectMapper.writeValueAsString(updateRequest);

        mvc.perform(put("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("Alice Updated"))
                .andExpect(jsonPath("$.tableNumber").value(5))
                .andExpect(jsonPath("$.numberOfGuests").value(3));
    }

    @Test
    public void updateBooking_BookingNotFound() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        TableBooking updateRequest = new TableBooking(null, "Alice Updated", futureTime, 5, 3);

        Mockito.when(bookingSvc.updateBooking(eq(1L), any(TableBooking.class)))
                .thenThrow(new ResourceNotFoundException("Booking not found"));

        String json = objectMapper.writeValueAsString(updateRequest);

        mvc.perform(put("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    public void deleteBooking_Success() throws Exception {
        Mockito.doNothing().when(bookingSvc).deleteBooking(1L);

        mvc.perform(delete("/bookings/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(bookingSvc, Mockito.times(1)).deleteBooking(1L);
    }

    @Test
    public void deleteBooking_BookingNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Booking not found"))
                .when(bookingSvc)
                .deleteBooking(1L);

        mvc.perform(delete("/bookings/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }
}
