package com.example.springsecuritydemo.controllers;

import com.example.springsecuritydemo.exceptions.AuthenticationException;
import com.example.springsecuritydemo.messages.JwtResponse;
import com.example.springsecuritydemo.messages.SignInRequest;
import com.example.springsecuritydemo.messages.SignUpRequest;
import com.example.springsecuritydemo.models.User;
import com.example.springsecuritydemo.security.JwtAuthenticationToken;
import com.example.springsecuritydemo.security.JwtUtils;
import com.example.springsecuritydemo.services.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class Auth {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Value("${jwt.header}")
    private String jwtHeader;

    @PostMapping("/signin")
    public ResponseEntity signIn(@RequestBody SignInRequest request) {
        final String username = request.getUsername();
        final String password = request.getPassword();

        final User user = jwtUserDetailsService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not existed!!!!"));

        try {
            authenticationManager.authenticate(
                    new JwtAuthenticationToken(
                            user.getId(),
                            username,
                            password
                    )
            );
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad Credentials!", e);
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody SignUpRequest request) {
        if (jwtUserDetailsService.existsByUsername(request.getUsername())) {
            return new ResponseEntity<>("Username is exists!", HttpStatus.BAD_REQUEST);
        }

        if (jwtUserDetailsService.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>("Email is exists!", HttpStatus.BAD_REQUEST);
        }

        jwtUserDetailsService.save(request.getName(), request.getUsername(), request.getPassword(), request.getEmail());

        return ResponseEntity.ok("User registered successfully!!!");
    }

    @GetMapping("/refresh")
    public ResponseEntity refreshToken(HttpServletRequest request) {
        String authToken = request.getHeader(jwtHeader).substring(7);
        String username = jwtUtils.getUsernameFromToken(authToken);

        if (jwtUtils.canTokenBeRefreshed(authToken)) {
            String refreshedToken = jwtUtils.refreshToken(authToken);
            return ResponseEntity.ok(new JwtResponse(refreshedToken));
        }

        return ResponseEntity.badRequest().body("Refresh failed!!");
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
