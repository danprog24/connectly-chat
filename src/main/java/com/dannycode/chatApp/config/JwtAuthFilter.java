package com.dannycode.chatApp.config;

import java.io.IOException; 
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.BlacklistedTokenRepo;
import com.dannycode.chatApp.repository.UserRepo;
import com.dannycode.chatApp.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    @SuppressWarnings("")
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        // JWT authentication logic here
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("=== Request path: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

         if (blacklistedTokenRepo.existsByToken(token)) {
            filterChain.doFilter(request, response);
            return; // reject token
        }   

        String username;


        
        try {
            username = jwtService.extractUsername(token);
            System.out.println("=== Extracted username: " + username);
        } catch (JwtException e) {
            System.out.println("=== JWT Exception: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            

            User user = userRepo.findByUsername(username).orElse(null);
            System.out.println("=== User found: " + (user != null ? user.getUsername() : "NULL"));
            System.out.println("=== Token valid: " + (user != null && jwtService.isTokenValid(token, user)));


            if(user != null && jwtService.isTokenValid(token, user)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("=== Authentication set successfully!");
            }
        
        }

    
        filterChain.doFilter(request, response);

    }
}
