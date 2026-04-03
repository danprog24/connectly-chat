package com.dannycode.chatApp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dannycode.chatApp.dto.UserProfileDTO;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final CloudinaryService cloudinaryService; // ✅ only real dependencies

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void setOnlineStatus(String username, boolean online) {
        User user = findByUsername(username);
        user.setOnline(online);
        userRepo.save(user);
        System.out.println("=== online status saved: " + username + " online status to " + online);
    }

    // In UserService.java
    public Map<String, Boolean> getOnlineStatuses() {
        return userRepo.findAll().stream()
            .collect(Collectors.toMap(User::getUsername, User::isOnline));
    }

    public UserProfileDTO getUserProfile(String username) {
        User user = findByUsername(username);
        return UserProfileDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .avatar(user.getAvatar())
            .online(user.isOnline())
            .createdAt(user.getCreatedAt())
            .mutualChatRoomsCount(user.getChatRooms().size())
            .build();
    }

    public String uploadAvatar(String username, MultipartFile file) throws IOException {
        User user = findByUsername(username);
        // ✅ use Cloudinary instead of local storage
        String avatarUrl = cloudinaryService.uploadAvatar(file, username);
        user.setAvatar(avatarUrl);
        userRepo.save(user);
        return avatarUrl;
    }

    public User register(User user) {
        if (userRepo.existsByEmail(user.getEmail()) || userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Email or Username already in use");
        }
        return userRepo.save(user);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> getFriends(String username) {
        User user = findByUsername(username);
        if (user.getFriends() == null) {
            return List.of();
        }
        return List.copyOf(user.getFriends());
    }
}