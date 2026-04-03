package com.dannycode.chatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dannycode.chatApp.model.BlacklistedToken;

public interface BlacklistedTokenRepo extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
}