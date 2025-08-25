package com.mechtech.automach.service;

import com.mechtech.automach.entity.User;
import com.mechtech.automach.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class getUsersDataserviceImpl implements getUsersDataservice {

    private static final Logger logger = LoggerFactory.getLogger(getUsersDataserviceImpl.class);
    private final UserRepository userRepository;

    @Autowired
    public getUsersDataserviceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserData(String username, String password) {
        logger.info("Attempting to find user with username: '{}' and password length: {}", username, password != null ? password.length() : 0);
        
        // Find user by username and password
        Optional<User> userOptional = userRepository.findByUsernameAndUserpassword(username, password);
        
        if (userOptional.isPresent()) {
            logger.info("User found: {}", username);
            return userOptional.get();
        } else {
            logger.warn("User NOT found with username: '{}' and password length: {}", username, password != null ? password.length() : 0);
            
            // Debug: Check if user exists with just username
            long userCount = userRepository.countByUsername(username);
            logger.info("Users found with username '{}': {}", username, userCount);
            
            return null;
        }
    }
}
