package com.dannycode.chatApp.service;

import java.util.List;

import com.dannycode.chatApp.model.FriendRequest;

public interface FriendRequestService {

    FriendRequest sendFriendRequest(Long senderId, Long receiverId);
    FriendRequest acceptFriendRequest(Long requestId, Long receiverId);
    List<FriendRequest> getPendingRequests(Long userId);

    void rejectFriendRequest(Long requestId);
    void cancelFriendRequest(Long requestId);

}
