package org.zeta.RestaurantManagement.servicetest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeta.RestaurantManagement.entity.User;
import org.zeta.RestaurantManagement.exception.InvalidCredentialsException;
import org.zeta.RestaurantManagement.exception.UserAlreadyExistsException;
import org.zeta.RestaurantManagement.exception.UserNotFoundException;
import org.zeta.RestaurantManagement.repository.UserRepository;
import org.zeta.RestaurantManagement.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepo;

    @InjectMocks
    UserService userSvc;

    @Test
    void createUserSuccess() {
        User user = new User(null, "Test User", "test@example.com", "password123", User.Role.CUSTOMER);
        when(userRepo.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepo.save(user)).thenReturn(user);

        User created = userSvc.createUser(user);

        assertNotNull(created);
        assertEquals("Test User", created.getName());
    }

    @Test
    void createUserDuplicateEmail() {
        User user = new User(null, "Test User", "test@example.com", "password123", User.Role.CUSTOMER);
        when(userRepo.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userSvc.createUser(user));
    }

    @Test
    void getUserByEmailFound() {
        User user = new User(1L, "Test User", "test@example.com", "password123", User.Role.CUSTOMER);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User found = userSvc.getUserByEmail("test@example.com");

        assertEquals(user, found);
    }

    @Test
    void getUserByEmailNotFound() {
        when(userRepo.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userSvc.getUserByEmail("nonexistent@example.com"));
    }

    @Test
    void authenticateSuccess() {
        User user = new User(1L, "Test User", "test@example.com", "password123", User.Role.CUSTOMER);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User authenticated = userSvc.authenticate("test@example.com", "password123");

        assertEquals(user, authenticated);
    }

    @Test
    void authenticateInvalidPasswordThrows() {
        User user = new User(1L, "Test User", "test@example.com", "password123", User.Role.CUSTOMER);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> userSvc.authenticate("test@example.com", "wrongpassword"));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void authenticateUserNotFoundThrows() {
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userSvc.authenticate("unknown@example.com", "anyPassword"));
        assertTrue(ex.getMessage().contains("User not found"));
    }
    @Test
    void authenticate_Success() {
        User user = new User(1L, "Alice", "alice@example.com", "pass123", User.Role.CUSTOMER);
        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        User authenticated = userSvc.authenticate("alice@example.com", "pass123");

        assertEquals(user, authenticated);
    }

    @Test
    void authenticate_InvalidPassword_Throws() {
        User user = new User(1L, "Alice", "alice@example.com", "pass123", User.Role.CUSTOMER);
        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> {
            userSvc.authenticate("alice@example.com", "wrongpass");
        });

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void authenticate_UserNotFound_Throws() {
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userSvc.authenticate("unknown@example.com", "any"));
    }

}
