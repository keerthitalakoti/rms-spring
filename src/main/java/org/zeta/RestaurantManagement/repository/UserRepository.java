package org.zeta.RestaurantManagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zeta.RestaurantManagement.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}