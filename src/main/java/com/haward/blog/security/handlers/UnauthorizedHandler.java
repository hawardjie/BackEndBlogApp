package com.haward.blog.security.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Unauthorized handler class for authentication entry point
 */
@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedHandler.class);

    /**
     * Commences an authentication scheme
     *
     * @param httpServletRequest Servlet request
     * @param httpServletResponse Servlet response for user agent to begin authentication
     * @param exp Exception that caused the invocation
     * @throws IOException IO exception
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException exp) throws IOException {
        LOGGER.error("Unauthorized. Error Message:", exp);
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
