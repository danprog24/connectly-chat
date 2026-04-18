package com.dannycode.chatApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dannycode.chatApp.auth.AuthResponse;
import com.dannycode.chatApp.auth.LoginRequest;
import com.dannycode.chatApp.auth.RegisterRequest;
import com.dannycode.chatApp.model.BlacklistedToken;
import com.dannycode.chatApp.repository.BlacklistedTokenRepo;
import com.dannycode.chatApp.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "https://connectly-chatz-nave.vercel.app"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @RequestBody RegisterRequest request
    ) {

        return ResponseEntity.ok(authService.register(request));

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok
        (authService.login(
            request.getUsername(), 
            request.getPassword())
        );
    }

   @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No token provided");
        }

        String token = authHeader.substring(7);

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);

        blacklistedTokenRepo.save(blacklistedToken);

        return ResponseEntity.ok("Logged out successfully");
    }
}