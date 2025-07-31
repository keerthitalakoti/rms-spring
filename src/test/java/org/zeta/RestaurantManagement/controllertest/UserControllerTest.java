package org.zeta.RestaurantManagement.controllertest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zeta.RestaurantManagement.controller.UserController;
import org.zeta.RestaurantManagement.entity.User;
import org.zeta.RestaurantManagement.exception.UserAlreadyExistsException;
import org.zeta.RestaurantManagement.exception.UserNotFoundException;
import org.zeta.RestaurantManagement.service.UserService;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUserSuccess() throws Exception {
        User user = new User(1L, "Alice", "alice@example.com", "password123", User.Role.WAITER);
        when(userSvc.createUser(any())).thenReturn(user);

        String json = """
            {
              "name": "Alice",
              "email": "alice@example.com",
              "password": "password123",
              "role": "WAITER"
            }
            """;

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("WAITER"));
    }

    @Test
    void createUserDuplicateEmail() throws Exception {
        when(userSvc.createUser(any()))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        String json = """
            {
              "name": "Bob",
              "email": "bob@example.com",
              "password": "password123",
              "role": "WAITER"
            }
            """;

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User already exists"));
    }

    @Test
    void createUserValidationFails() throws Exception {
        // Missing required 'email' field triggers validation error
        String json = """
            {
              "name": "Charlie",
              "password": "password123",
              "role": "WAITER"
            }
            """;

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void getAllUsers() throws Exception {
        User u1 = new User(1L, "Alice", "alice@example.com", "password123", User.Role.WAITER);
        User u2 = new User(2L, "Bob", "bob@example.com", "password123", User.Role.CHEF);
        when(userSvc.getAllUsers()).thenReturn(List.of(u1, u2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"));
    }

    @Test
    void getUserByEmailSuccess() throws Exception {
        User user = new User(1L, "Alice", "alice@example.com", "password123", User.Role.WAITER);
        when(userSvc.getUserByEmail(eq("alice@example.com"))).thenReturn(user);

        mvc.perform(get("/users/alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.role").value("WAITER"));
    }

    @Test
    void getUserByEmailNotFound() throws Exception {
        when(userSvc.getUserByEmail(eq("nonexistent@example.com")))
                .thenThrow(new UserNotFoundException("User not found with email: nonexistent@example.com"));

        mvc.perform(get("/users/nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with email: nonexistent@example.com"));
    }
}
