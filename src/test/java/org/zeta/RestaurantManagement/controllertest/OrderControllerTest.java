package org.zeta.RestaurantManagement.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zeta.RestaurantManagement.controller.OrderController;
import org.zeta.RestaurantManagement.entity.Order;
import org.zeta.RestaurantManagement.exception.OrderNotFoundException;
import org.zeta.RestaurantManagement.service.OrderService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;  // For JSON serialization

    @Test
    public void createOrder_Success() throws Exception {
        Order order = new Order(1L, 3, Arrays.asList("Coke", "Pizza"), Order.Status.PLACED);

        Mockito.when(orderService.createOrder(any(Order.class))).thenReturn(order);

        String json = objectMapper.writeValueAsString(order);

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.tableNumber").value(order.getTableNumber()))
                .andExpect(jsonPath("$.items[0]").value("Coke"))
                .andExpect(jsonPath("$.items[1]").value("Pizza"))
                .andExpect(jsonPath("$.status").value("PLACED"));
    }

    @Test
    public void createOrder_InvalidPayload_ReturnsBadRequest() throws Exception {
        // Empty JSON or missing required fields simulates invalid payload
        String invalidJson = "{}";

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllOrders_ReturnsOrders() throws Exception {
        Order order1 = new Order(1L, 3, Arrays.asList("Coke"), Order.Status.PLACED);
        Order order2 = new Order(2L, 4, Arrays.asList("Burger"), Order.Status.SERVED);

        Mockito.when(orderService.getAllOrders()).thenReturn(Arrays.asList(order1, order2));

        mvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tableNumber").value(3))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].tableNumber").value(4));
    }

    @Test
    public void getOrder_ById_Success() throws Exception {
        Order order = new Order(10L, 5, Arrays.asList("Tea"), Order.Status.SERVED);

        Mockito.when(orderService.getOrder(10L)).thenReturn(order);

        mvc.perform(get("/orders/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.tableNumber").value(5))
                .andExpect(jsonPath("$.items[0]").value("Tea"))
                .andExpect(jsonPath("$.status").value("SERVED"));
    }

    @Test
    public void getOrder_ById_NotFound() throws Exception {
        Mockito.when(orderService.getOrder(999L)).thenThrow(new OrderNotFoundException("Order not found"));

        mvc.perform(get("/orders/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void updateOrderStatus_Success() throws Exception {
        Order updatedOrder = new Order(1L, 3, Arrays.asList("Pizza", "Soda"), Order.Status.IN_KITCHEN);

        Mockito.when(orderService.updateOrderStatus(eq(1L), eq(Order.Status.IN_KITCHEN)))
                .thenReturn(updatedOrder);

        String json = """
                {
                  "status": "IN_KITCHEN"
                }
                """;

        mvc.perform(put("/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_KITCHEN"));
    }

    @Test
    public void updateOrderStatus_MissingStatus_ReturnsBadRequest() throws Exception {
        String json = """
                {
                  "tableNumber": 3,
                  "items": ["Pizza"]
                }
                """;  // No "status" field

        mvc.perform(put("/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateOrderStatus_NotFound() throws Exception {
        Mockito.when(orderService.updateOrderStatus(eq(99L), eq(Order.Status.SERVED)))
                .thenThrow(new OrderNotFoundException("Order not found"));

        String json = """
                {
                  "status": "SERVED"
                }
                """;

        mvc.perform(put("/orders/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
