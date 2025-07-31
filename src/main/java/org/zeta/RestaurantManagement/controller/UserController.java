package org.zeta.RestaurantManagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeta.RestaurantManagement.entity.User;
import org.zeta.RestaurantManagement.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Received request to create user with email '{}'", user.getEmail());
        User created = userService.createUser(user);
        logger.info("User created successfully with id {}", created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getAllUsers() {
        logger.info("Received request to get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.info("Received request to get user by email '{}'", email);
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
