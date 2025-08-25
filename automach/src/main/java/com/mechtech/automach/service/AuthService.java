package com.mechtech.automach.service;

import com.mechtech.automach.dto.UserCredentialsDTO;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Service interface for authentication-related operations, such as generating and refreshing JWT tokens.
 */
public interface AuthService {
    
    /**
     * Logs in a user and generates a JWT token.
     *
     * @param credentials UserCredentialsDTO containing login credentials.
     * @param headers HTTP response object for setting headers.
     * @param errorMessage List to collect error messages.
     * @return Generated JWT token or error message.
     */
    Map<String, Object> login(UserCredentialsDTO credentials, HttpServletResponse headers, List<String> errorMessage);

    /**
     * Refreshes the JWT token.
     *
     * @param token Refresh token.
     * @param response HTTP response object for setting headers.
     * @param errorMessage List to collect error messages.
     * @return New JWT token or error message.
     */
    Map<String, Object> refreshToken(String token, HttpServletResponse response, List<String> errorMessage);
}
