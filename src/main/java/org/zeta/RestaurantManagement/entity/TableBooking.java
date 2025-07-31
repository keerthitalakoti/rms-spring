package org.zeta.RestaurantManagement.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "table_bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TableBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Booking time is required")
    @FutureOrPresent(message = "Booking time must be in the present or future")
    private LocalDateTime bookingTime;

    @Min(value = 1, message = "Table number must be at least 1")
    private int tableNumber;

    @Min(value = 1, message = "Number of guests must be at least 1")
    private int numberOfGuests;
}
