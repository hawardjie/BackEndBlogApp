package com.haward.blog.security;

import com.haward.blog.security.userdetails.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * AuthComponent class provides the APIs related to current active user and its JWT token
 */
@Component
public class AuthComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthComponent.class);

    @Value("${app.token.secret}")
    private String secret; // value is defined in application.properties file

    @Value("${app.token.expiration}")
    private int expiration; // value is defined in application.properties file

    /**
     * Generate JWT token
     *
     * @param authentication Authentication to get user info for generating token
     * @return JWT token
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration*1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Check if JWT token is valid or not
     *
     * @param token JWT token to be validated
     * @return True if token is valid. Else, return false
     */
    public boolean isTokenValid(String token) {
        Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        return true;
    }

    /**
     * Get username given its related token
     *
     * @param token JWT token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Get current active user details
     *
     * @return User details
     */
    public Optional<CustomUserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return Optional.of(user);
    }
}
