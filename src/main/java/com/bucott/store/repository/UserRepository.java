package com.bucott.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bucott.store.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(String identifier);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    boolean existsByUsernameOrEmail(String identifier);
    
}
