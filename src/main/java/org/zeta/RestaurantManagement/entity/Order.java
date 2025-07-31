package org.zeta.RestaurantManagement.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Order {

    public enum Status { PLACED, IN_KITCHEN, SERVED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Table number must be at least 1")
    private int tableNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "item")
    @NotEmpty(message = "Order must have at least one item")
    private List<String> items;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status ;
}
