package com.mechtech.automach.service;

import com.mechtech.automach.dto.UserLoginDetailsDTO;
import com.mechtech.automach.entity.User;
import com.mechtech.automach.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserLoginDetailsServiceImpl implements UserLoginDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateUserDetails(Map<String, Object> loginDetails) {
        String username = "";
        String password = "";
        Map<String, Object> response = new HashMap<>();

        if (loginDetails != null && !loginDetails.isEmpty()) {
            if (loginDetails.containsKey("username") && loginDetails.get("username") != null
                    && !loginDetails.get("username").toString().isEmpty()) {
                username = loginDetails.get("username").toString();
            }
            if (loginDetails.containsKey("password") && loginDetails.get("password") != null
                    && !loginDetails.get("password").toString().isEmpty()) {
                password = loginDetails.get("password").toString();
            }
        }
        Optional<User> userOptional = Optional.empty();
        try {
            // Find user by username first
            userOptional = userDetailsRepository.findByUsername(username);
            
            // If user found, verify password
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!password.equals(user.getPassword())) {
                    // Password doesn't match
                    response.put("error", "Invalid username or password");
                    return response;
                }
                
                LocalDate expiryDate = user.getExpirydate();

                if (expiryDate != null && (expiryDate.isAfter(LocalDate.now()) || expiryDate.isEqual(LocalDate.now()))) {
                    // Map User entity to UserLoginDetailsDTO
                    UserLoginDetailsDTO userLoginDetails = mapUserToUserLoginDetailsDTO(user);
                    response.put("UserLoginDetails", userLoginDetails);
                } else {
                    response.put("error", "User subscription Expired");
                }
            } else {
                response.put("error", "Invalid username or password");
            }
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getMessage());
            response.put("error", "Database error occurred");
            return response;
        }

        return response;
    }

    private UserLoginDetailsDTO mapUserToUserLoginDetailsDTO(User user) {
        UserLoginDetailsDTO dto = new UserLoginDetailsDTO();
        // Map fields from User entity to UserLoginDetailsDTO
        // Note: Some fields may need to be set to default values or fetched from other sources
        dto.setUserId(user.getUsername());
        dto.setMobileNo(user.getMobileno());
        dto.setSubscriberExpiryDate(user.getExpirydate());
        dto.setRoleName("User"); // Default role
        dto.setFirmName("Default Firm"); // Default firm name
        
        return dto;
    }
}
