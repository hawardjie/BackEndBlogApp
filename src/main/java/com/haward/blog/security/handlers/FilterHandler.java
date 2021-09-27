package com.haward.blog.security.handlers;

import com.haward.blog.security.AuthComponent;
import com.haward.blog.security.userdetails.CustomUserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter handler class to guarantee a single execution per request dispatch
 */
public class FilterHandler extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterHandler.class);

    @Autowired
    private AuthComponent authComponent; // user authorization component

    @Autowired
    private CustomUserDetailsImpl customUserDetails; // to provide user service

    /**
     * Filter request to authenticate user.
     * It is invoked once per request within a single request thread
     *
     * @param request HTTP Servlet request
     * @param response HTTP Servlet response
     * @param filterChain Invocation chain of a filtered request
     * @throws ServletException Servlet exception
     * @throws IOException IO Exception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Get and validate the token
        String jwtToken = getJwtToken(request);
        if (jwtToken != null && authComponent.isTokenValid(jwtToken)) {
            String username = authComponent.getUsernameFromToken(jwtToken);
            if (username != null) {
                // Get user details
                UserDetails userDetails = customUserDetails.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                // Inform Spring Security to certify that user has been authenticated
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // Invoke the chain with the passing servlet request and response
        filterChain.doFilter(request, response);
    }

    /**
     * Helper class to get JWT token from the header
     *
     * @param request Request information for HTTP servlet
     * @return JWT token for user authorization
     */
    private String getJwtToken(HttpServletRequest request) {
        // We are interested to get the header "Authorization" only
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ") || header.equals("Bearer null")) {
            return null;  // if header is invalid, return null
        }
        // Trim the prefix "Bearer " so it returns the token only
        return header.replace("Bearer ", "");
    }
}
