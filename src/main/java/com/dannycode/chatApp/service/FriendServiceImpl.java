package com.dannycode.chatApp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.UserRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final UserRepo userRepo;

    @Transactional
    @Override
    public void createFriendship(Long userId1, Long userId2) {
        User user1 = userRepo.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId1));
        User user2 = userRepo.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId2));

        if (!user1.getFriends().contains(user2)) {
            user1.getFriends().add(user2);
        }

        if (!user2.getFriends().contains(user1)) {
            user2.getFriends().add(user1);
        }

        userRepo.save(user1);
        userRepo.save(user2);
    }
}