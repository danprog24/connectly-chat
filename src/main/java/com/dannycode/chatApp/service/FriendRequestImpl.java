package com.dannycode.chatApp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dannycode.chatApp.model.FriendRequest;
import com.dannycode.chatApp.model.FriendRequestStatus;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.FriendRequestRepo;
import com.dannycode.chatApp.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendRequestImpl implements FriendRequestService {

    private final FriendService friendService;
    private final FriendRequestRepo friendRequestRepo;
    private final UserRepo userRepo;

    @Override
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself.");
        }

        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        User receiver = userRepo.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);

        return friendRequestRepo.save(request);
    }

    @Override
    public List<FriendRequest> getPendingRequests(Long receiverId) {
        return friendRequestRepo.findByReceiverIdAndStatus(receiverId, FriendRequestStatus.PENDING);
    }
   @Override
    public FriendRequest acceptFriendRequest(Long requestId, Long receiverId) {
        FriendRequest request = friendRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        // compare receiver's ID to authenticated user's ID
        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendService.createFriendship(
                request.getSender().getId(),
                request.getReceiver().getId()
        );

        return friendRequestRepo.save(request);
    }
    @Override
    public void rejectFriendRequest(Long requestId) {

        FriendRequest request = friendRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        request.setStatus(FriendRequestStatus.REJECTED);

        friendRequestRepo.save(request);
    }

    @Override
    public void cancelFriendRequest(Long requestId) {

        FriendRequest request = friendRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        request.setStatus(FriendRequestStatus.CANCELED);

        friendRequestRepo.save(request);
    }
}