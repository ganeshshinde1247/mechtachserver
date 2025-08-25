package com.mechtech.automach.repository;

import com.mechtech.automach.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndUserpassword(String username, String userpassword);
    
    // Debug method to check if username exists
    long countByUsername(String username);
    
    // Debug method to find by username only
    Optional<User> findByUsername(String username);
}
