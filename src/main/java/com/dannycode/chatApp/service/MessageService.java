package com.dannycode.chatApp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dannycode.chatApp.model.ChatRoom;
import com.dannycode.chatApp.model.Message;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.ChatRoomRepo;
import com.dannycode.chatApp.repository.MessageRepo;
import com.dannycode.chatApp.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final ChatRoomRepo chatRoomRepo;
    private final UserRepo userRepo;
    private final MessageRepo messageRepo;

    // helper — get or create room
    private ChatRoom getOrCreateRoom(String roomName) {
        return chatRoomRepo.findByName(roomName)
            .orElseGet(() -> {
                ChatRoom newRoom = ChatRoom.builder()
                    .name(roomName)
                    .build();
                return chatRoomRepo.save(newRoom);
            });
    }

    public Message sendMessage(String roomName, String username, String content) {
        ChatRoom room = getOrCreateRoom(roomName); // ✅ create if not exists

        User sender = userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Message message = Message.builder()
            .chatRoom(room)
            .sender(sender)
            .content(content)
            .timestamp(LocalDateTime.now())
            .build();

        return messageRepo.save(message);
    }

    public List<Message> getMessagesByRoom(String roomName) {
        ChatRoom room = getOrCreateRoom(roomName); // ✅ create if not exists
        return messageRepo.findByChatRoomOrderByTimestampAsc(room);
    }
}