package com.dannycode.chatApp.controller;

import com.dannycode.chatApp.dto.UserProfileDTO;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.UserRepo;
import com.dannycode.chatApp.service.UserService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "https://connectly-chatz-nave.vercel.app"})
public class UserController {

    private final UserService userService; 
    private final UserRepo userRepo;


   @GetMapping("/online-status")
    public ResponseEntity<Map<String, Boolean>> getOnlineStatuses() {
        List<User> users = userRepo.findAll();
        Map<String, Boolean> statuses = users.stream()
            .collect(Collectors.toMap(User::getUsername, User::isOnline));
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileDTO> getUserProfile(
        @PathVariable String username) {
            UserProfileDTO profile = userService.getUserProfile(username);
        return ResponseEntity.ok(profile);
}

    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(
        @AuthenticationPrincipal User user,
        @RequestParam("file") MultipartFile file) throws IOException {
    System.out.println("=== Upload avatar called for: " + user.getUsername()); // 👈
    System.out.println("=== File name: " + file.getOriginalFilename()); // 👈
    System.out.println("=== File size: " + file.getSize()); // 👈
    String avatarUrl = userService.uploadAvatar(user.getUsername(), file);
    return ResponseEntity.ok(avatarUrl);
}
    // get all users
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);

    }

    @GetMapping("/{username}/friends")
    public ResponseEntity<List<User>> getFriendsByUsername(@PathVariable String username) {
        List<User> friends = userService.getFriends(username);
        return ResponseEntity.ok(friends);
    }

}
