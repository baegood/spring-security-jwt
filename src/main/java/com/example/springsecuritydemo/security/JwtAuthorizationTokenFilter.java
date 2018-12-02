package com.example.springsecuritydemo.security;

import com.example.springsecuritydemo.services.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationTokenFilter.class);

    private static final String HEADER_PREFIX = "Bearer ";

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.info("authentication for : {}", request.getRequestURL());

        final String requestHeader = request.getHeader(tokenHeader);

        String authToken = getAuthToken(requestHeader);
        String username = getUsernameFromToken(authToken);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Authoring user : {}", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(authToken, userDetails)) {
                JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                logger.info("auth user : {}", username);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getAuthToken(String requestHeader) {
        String authToken = null;

        if (requestHeader != null && requestHeader.startsWith(HEADER_PREFIX)) {
            authToken = requestHeader.substring(HEADER_PREFIX.length());
        }

        return authToken;
    }

    private String getUsernameFromToken(String token) {
        String username = null;

        try {
            username = jwtUtils.getUsernameFromToken(token);
        } catch (IllegalArgumentException e) {
            logger.error("Error occured during get username from token : {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token is expired : {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Token is invalid : {}", e.getMessage());
        }

        return username;
    }
}
