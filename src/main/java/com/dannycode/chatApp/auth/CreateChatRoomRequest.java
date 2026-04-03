package com.dannycode.chatApp.auth;

import java.util.List;

public record CreateChatRoomRequest(
    String roomName,
    List<String> usernames
) {

}
