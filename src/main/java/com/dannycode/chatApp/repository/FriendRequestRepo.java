package com.dannycode.chatApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dannycode.chatApp.model.FriendRequestStatus;
import com.dannycode.chatApp.model.FriendRequest;
import com.dannycode.chatApp.model.User;

public interface FriendRequestRepo extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverIdAndStatus(Long userId, FriendRequestStatus status);
    List<FriendRequest> findBySenderIdAndStatus(User sender, FriendRequestStatus status);
    
    
}