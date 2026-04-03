package com.dannycode.chatApp.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dannycode.chatApp.model.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${api.key}")
    private String apiKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours session expiration

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", String.valueOf(user.getId()))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(Keys.hmacShaKeyFor(apiKey.getBytes()), SignatureAlgorithm.HS256)
            .compact();

    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(apiKey.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean isTokenValid(String token, User user) {
       try {
        final String username = extractUsername(token);

        boolean matches = username.equals(user.getUsername());
        boolean notExpired = !isTokenExpired(token);

        return matches && notExpired;
       }catch (JwtException e) {
        return false; // Token is invalid
       }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(apiKey.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();

        return expiration.before(new Date());
    }

}
