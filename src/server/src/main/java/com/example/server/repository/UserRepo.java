package com.example.server.repository;

import com.example.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByUserUsername(String username);
    boolean existsByUserUsername(String username);
    boolean existsByUserEmail(String email);
    long countByCreatedAtAfter(LocalDateTime date);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
