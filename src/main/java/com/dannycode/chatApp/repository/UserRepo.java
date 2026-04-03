package com.dannycode.chatApp.repository;

import com.dannycode.chatApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    List<User> findAllByUsernameIn(List<String> usernames);

}