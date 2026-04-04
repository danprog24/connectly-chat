package com.dannycode.chatApp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.dannycode.chatApp.model.FriendRequest;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.service.FriendRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = {"http://localhost:5173", "https://connectly-chatz-nave.vercel.app"})
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    // Send a friend request
   @PostMapping("/send/{receiverId}")
    public ResponseEntity<FriendRequest> sendFriendRequest(
        @AuthenticationPrincipal User sender, // ✅ get from JWT, not request param
        @PathVariable Long receiverId) {

        FriendRequest request = friendRequestService.sendFriendRequest(sender.getId(), receiverId);
        return ResponseEntity.ok(request);
    }


    // Accept a friend request
    @PutMapping("/accept/{requestId}")
    public ResponseEntity<FriendRequest> acceptFriendRequest(
            @AuthenticationPrincipal User receiver, // ✅ add this
            @PathVariable Long requestId) {
        FriendRequest request = friendRequestService.acceptFriendRequest(requestId, receiver.getId());
        return ResponseEntity.ok(request);
    }

    // Reject a friend request
    @PutMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectFriendRequest(
            @PathVariable Long requestId) {

        friendRequestService.rejectFriendRequest(requestId);
        return ResponseEntity.ok("Friend request rejected");
    }

    // Cancel a sent friend request
    @DeleteMapping("/cancel/{requestId}")
    public ResponseEntity<String> cancelFriendRequest(
            @PathVariable Long requestId) {

        friendRequestService.cancelFriendRequest(requestId);
        return ResponseEntity.ok("Friend request cancelled");
    }

   // Get all pending friend requests for logged in user
    @GetMapping("/pending")
    public ResponseEntity<List<FriendRequest>> getPendingRequests(
            @AuthenticationPrincipal User receiver) {
        List<FriendRequest> requests = friendRequestService.getPendingRequests(receiver.getId());
        return ResponseEntity.ok(requests);
    }
}