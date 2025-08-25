package com.mechtech.automach.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for JWT token operations including generation, validation, and claim extraction.
 */
@Component
public class JWTUtils {
    // Secret key for signing JWTs
    private SecretKey key;
    // Token expiration time in milliseconds
    private long expirationTime;

    /**
     * Constructor initializes the secret key for JWT operations.
     */
    public JWTUtils(@Value("${jwt.secret}") String secretKey, 
                   @Value("${jwt.expiration}") long expirationTime) {
        this.expirationTime = expirationTime;
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * Generates a JWT token for the provided user details.
     *
     * @param userDetails User details for whom the token is generated.
     * @return Generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        // Build a JWT token with subject, issuance time, expiration time, and signing key
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token JWT token from which to extract the username.
     * @return Extracted username.
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Generic method to extract claims from a JWT token using the provided function.
     *
     * @param token           JWT token from which to extract claims.
     * @param claimsTFunction Function to apply for extracting specific claims.
     * @param <T>             Type of the extracted claim.
     * @return Extracted claim.
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        // Parse the token, verify with the key, and extract the specified claim using the provided function
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    /**
     * Checks if the given JWT token is valid for the provided user details.
     *
     * @param token       JWT token to validate.
     * @param userDetails User details to compare with the token.
     * @return True if the token is valid; false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails, String username) {
        // Extract the username from the token and check for username match and non-expiration
        if (username == null || username.isEmpty()) {
            username = extractUsername(token);
        }
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token JWT token to check for expiration.
     * @return True if the token is expired; false otherwise.
     */
    public boolean isTokenExpired(String token) {
        // Check if the expiration time claim is before the current date and time
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
