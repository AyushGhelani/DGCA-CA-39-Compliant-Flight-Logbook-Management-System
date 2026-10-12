package com.dgca.flightlogbook.service;

import com.dgca.flightlogbook.dto.AuthResponse;
import com.dgca.flightlogbook.dto.LoginRequest;
import com.dgca.flightlogbook.dto.RegisterRequest;
import com.dgca.flightlogbook.entity.Role;
import com.dgca.flightlogbook.entity.User;
import com.dgca.flightlogbook.exception.BusinessValidationException;
import com.dgca.flightlogbook.repository.UserRepository;
import com.dgca.flightlogbook.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user registration and JWT-based authentication.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Register a new user with BCrypt-hashed password.
     */
    @Transactional
    public User register(RegisterRequest request) {
        // Uniqueness checks
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessValidationException(
                    "Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessValidationException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        // Parse and validate role
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessValidationException(
                    "Invalid role '" + request.getRole() + "'. Must be STUDENT, CFI, or ADMIN");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .fullName(request.getFullName())
                .email(request.getEmail())
                .licenseNumber(request.getLicenseNumber())
                .role(role)
                .build();

        return userRepository.save(user);
    }

    /**
     * Authenticate a user and return a JWT.
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessValidationException("User not found"));

        return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getFullName(), user.getRole().name());
    }
}
