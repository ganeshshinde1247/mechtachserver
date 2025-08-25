package com.mechtech.automach.service;

import com.mechtech.automach.dto.UserCredentialsDTO;
import com.mechtech.automach.dto.UserLoginDetailsDTO;
import com.mechtech.automach.entity.User;
import com.mechtech.automach.repository.UserDetailsRepository;
import com.mechtech.automach.utils.JWTUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for authentication-related operations, such as generating and refreshing JWT tokens.
 */
@Service
public class AuthServiceImpl implements AuthService {
    public static final String AUTHORIZATION = "authorization";
    public static final String MESSAGE = "message";
    public static final String OWNER_APP = "OwnerApp";
    public static final String MOBILE_NO = "9090909090";
    public static final String OWNCODE = "ownCode";

    private final JWTUtils jwtUtils;
    private final UserDetailsRepository userDetailsRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserLoginDetailsService userLoginDetailsService;

    @Autowired
    public AuthServiceImpl(JWTUtils jwtUtils, UserDetailsRepository userDetailsRepository, 
                          ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                          UserLoginDetailsService userLoginDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsRepository = userDetailsRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userLoginDetailsService = userLoginDetailsService;
    }

    /**
     * Logs in a user and generates a JWT token.
     *
     * @param credentials UserCredentialsDTO containing login credentials.
     * @param headers HTTP response object for setting headers.
     * @param errorMessage List to collect error messages.
     * @return Generated JWT token or error message.
     */
    @Override
    public Map<String, Object> login(UserCredentialsDTO credentials, HttpServletResponse headers,
                                    List<String> errorMessage) {

        Map<String, Object> userDetailsdecrypt = null;
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            // Convert UserCredentialsDTO to Map for compatibility with existing validateUserDetails method
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("username", credentials.getUsername());
            requestData.put("password", credentials.getPassword());
            
            // Validate username and set subscriberId
            if (credentials.getUsername() != null && !credentials.getUsername().isEmpty()) {
                requestData.put("subscriberId", credentials.getUsername());
            }

            // Validate user details from service check first and return data with error
            Map<String, Object> UserLoginDetailsdata = userLoginDetailsService.validateUserDetails(requestData);
            if (UserLoginDetailsdata.get("error") != null && !UserLoginDetailsdata.get("error").toString().isEmpty()) {
                return UserLoginDetailsdata;
            }

            // Map the user login details from jwt user data
            UserLoginDetailsDTO userLoginDetailsVM = (UserLoginDetailsDTO) UserLoginDetailsdata.get("UserLoginDetails");

            // Prepare response data which is getting from validation method
            responseData.put("userId", userLoginDetailsVM.getUserId());
           // responseData.put("subscriberCode", userLoginDetailsVM.getSubscriberCode());
            responseData.put("mobileNo", userLoginDetailsVM.getMobileNo());
            responseData.put("subscriberExpiryDate", userLoginDetailsVM.getSubscriberExpiryDate());
            //responseData.put("roleCode", userLoginDetailsVM.getRoleCode());
            //responseData.put("roleName", userLoginDetailsVM.getRoleName());
            //responseData.put("locationCode", userLoginDetailsVM.getLocationCode());
            //responseData.put("locationName", userLoginDetailsVM.getLocationName());
            //responseData.put("appCode", userLoginDetailsVM.getAppCode());


            if (credentials.getUsername().equals(OWNER_APP)) {
                userDetailsdecrypt = requestData;
            } else {
                userDetailsdecrypt = requestData;
            }

            if (userDetailsdecrypt != null) {
                String mobileNo = userLoginDetailsVM.getMobileNo();

                // Check if the user exists in the repository
                Optional<User> optionalUser = userDetailsRepository.findByMobileno(mobileNo);

                // If the user does not exist, create a new user
                if (optionalUser.isEmpty()) {
                    saveJWTUser(userLoginDetailsVM); // Save the user only once

                    // Re-fetch user after saving
                    optionalUser = userDetailsRepository.findByMobileno(mobileNo);

                    if (optionalUser.isEmpty()) {
                        errorMessage.add("User not found with mobile number: " + mobileNo);
                    }
                }

                    if (optionalUser.isPresent()) {
                        var userDetails = optionalUser.get();

                        // Proceed with authentication and token generation
                        if (userDetails.getMobileno().equalsIgnoreCase(mobileNo)) {
                            var user = userDetailsRepository.findByUsername(userDetails.getUsername())
                                    .orElseThrow();

                            var jwt = jwtUtils.generateToken(user);
                            headers.addHeader(AUTHORIZATION, jwt.toString());

                            // Add response data for a successful login
                            responseData.put(OWNCODE, userDetails.getOwncode());
                            responseData.put("expirationTime", "24Hr");
                            responseData.put(MESSAGE, "Successfully logged In");
                            responseData.put(AUTHORIZATION, jwt.toString());
                        } else {
                            errorMessage.add("Enter valid subscriber ID");
                        }
                    }
            }
        } catch (Exception e) {
            errorMessage.add(e.getMessage());
        }

        if (!errorMessage.isEmpty()) {
            responseData = new HashMap<>();
            responseData.put("error", errorMessage);
        }
        return responseData;
    }

    /**
     * Refreshes the JWT token.
     *
     * @param token Refresh token.
     * @param response HTTP response object for setting headers.
     * @param errorMessage List to collect error messages.
     * @return New JWT token or error message.
     */
    @Override
    public Map<String, Object> refreshToken(String token, HttpServletResponse response, List<String> errorMessage) {
        if (jwtUtils.isTokenExpired(token)) {
            errorMessage.add("Token has expired.");
            return null;
        }

        // Extract username from token and find user
        String username = jwtUtils.extractUsername(token);
        Optional<User> userOptional = userDetailsRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            errorMessage.add("User not found");
            return null;
        }

        // Generate a new token
        String newToken = jwtUtils.generateToken(userOptional.get());
        response.addHeader(AUTHORIZATION, newToken);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", newToken);
        responseData.put("message", "Token refreshed successfully");
        return responseData;
    }

    // Method to save JWT User
    @Transactional
    public boolean saveJWTUser(UserLoginDetailsDTO userLoginDetailsVM) {
        User users = new User(); // Created for JWT user info save

        users.setUserpassword(passwordEncoder.encode(userLoginDetailsVM.getUserId())); // Using userId as password
        users.setMobileno(userLoginDetailsVM.getMobileNo()); // Set mobile number
        users.setUsername(userLoginDetailsVM.getMobileNo()); // Set username
        users.setExpirydate(userLoginDetailsVM.getSubscriberExpiryDate()); // Set expiry date

        // Save the user to the repository
        User savedUser = userDetailsRepository.save(users);

        // Return true if the user was saved successfully, false otherwise
        return savedUser != null;
    }
}
