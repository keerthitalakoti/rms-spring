package org.zeta.RestaurantManagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeta.RestaurantManagement.entity.User;
import org.zeta.RestaurantManagement.exception.BadRequestException;
import org.zeta.RestaurantManagement.exception.InvalidCredentialsException;
import org.zeta.RestaurantManagement.exception.UserAlreadyExistsException;
import org.zeta.RestaurantManagement.exception.UserNotFoundException;
import org.zeta.RestaurantManagement.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public User createUser(User user) {
        logger.info("Creating user with email '{}'", user.getEmail());

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            logger.warn("User creation failed: email is missing");
            throw new BadRequestException("Email is required");
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            logger.warn("User creation failed: duplicate email '{}'", user.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email");
        }

        User savedUser = userRepo.save(user);

        logger.info("User created successfully with id {}", savedUser.getId());
        return savedUser;
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepo.findAll();
    }

    public User getUserByEmail(String email) {
        logger.info("Fetching user by email '{}'", email);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email '{}'", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
    }

    public User authenticate(String email, String password) {
        logger.info("Authenticating user with email '{}'", email);
        User user = getUserByEmail(email);
        if (!user.getPassword().equals(password)) {
            logger.warn("Authentication failed for user '{}': invalid credentials", email);
            throw new InvalidCredentialsException("Invalid credentials");
        }
        logger.info("User '{}' authenticated successfully", email);
        return user;
    }
}
