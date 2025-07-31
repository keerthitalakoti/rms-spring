package org.zeta.RestaurantManagement.servicetest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeta.RestaurantManagement.entity.Order;
import org.zeta.RestaurantManagement.exception.BadRequestException;
import org.zeta.RestaurantManagement.exception.OrderNotFoundException;
import org.zeta.RestaurantManagement.repository.OrderRepository;
import org.zeta.RestaurantManagement.service.OrderService;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepo;

    @InjectMocks
    OrderService orderSvc;

    @Test
    void createOrderSuccess() {
        Order order = new Order();
        order.setTableNumber(3);
        order.setItems(List.of("Coke", "Pizza"));
        when(orderRepo.save(order)).thenReturn(order);

        Order created = orderSvc.createOrder(order);

        assertEquals(Order.Status.PLACED, created.getStatus());
        verify(orderRepo).save(order);
    }

    @Test
    void createOrderEmptyItems() {
        Order order = new Order();
        order.setTableNumber(3);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> orderSvc.createOrder(order));

        assertEquals("Order must contain at least one item", ex.getMessage());
        verify(orderRepo, never()).save(any());
    }

    @Test
    void getOrderByIdFound() {
        Order order = new Order(1L, 3, List.of("Coke", "Pizza"), Order.Status.PLACED);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        Order found = orderSvc.getOrder(1L);

        assertEquals(order, found);
        verify(orderRepo).findById(1L);
    }

    @Test
    void getOrderByIdNotFound() {
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());

        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class, () -> orderSvc.getOrder(1L));

        assertEquals("Order not found with id: 1", ex.getMessage());
        verify(orderRepo).findById(1L);
    }

    @Test
    void updateStatusSuccess() {
        Long id = 1L;
        Order existing = new Order(id, 3, List.of("Coke"), Order.Status.PLACED);
        when(orderRepo.findById(id)).thenReturn(Optional.of(existing));
        when(orderRepo.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Order updated = orderSvc.updateOrderStatus(id, Order.Status.IN_KITCHEN);

        assertEquals(Order.Status.IN_KITCHEN, updated.getStatus());
        verify(orderRepo).findById(id);
        verify(orderRepo).save(existing);
    }

    @Test
    void updateStatusNullStatusThrows() {
        Long id = 1L;
        // We do not need to stub findById here because exception happens before repo call

        BadRequestException ex = assertThrows(BadRequestException.class, () -> orderSvc.updateOrderStatus(id, null));

        assertEquals("Order status must be provided", ex.getMessage());
        verify(orderRepo, never()).findById(any());
        verify(orderRepo, never()).save(any());
    }

    @Test
    void updateStatusOrderNotFoundThrows() {
        Long id = 99L;
        when(orderRepo.findById(id)).thenReturn(Optional.empty());

        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class, () -> orderSvc.updateOrderStatus(id, Order.Status.SERVED));

        assertEquals("Order not found with id: 99", ex.getMessage());
        verify(orderRepo).findById(id);
        verify(orderRepo, never()).save(any());
    }

}
