package com.dannycode.chatApp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.dannycode.chatApp.auth.AuthResponse;
import com.dannycode.chatApp.auth.RegisterRequest;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user with hashed password and return JWT token.
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Build user entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(hashedPassword)
                .build();

        // Save to DB
        userRepo.save(user);

        // register without JWT token
       // String token = jwtService.generateToken(user);

        return new AuthResponse(null, "User registered successfully");
    }

    /*
     *  Authenticate user credentials and return JWT token.
     */
    public AuthResponse login(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, "Login successful");
    }
}
