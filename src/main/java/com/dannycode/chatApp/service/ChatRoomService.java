package com.dannycode.chatApp.service;

import com.dannycode.chatApp.repository.ChatRoomRepo;
import com.dannycode.chatApp.repository.UserRepo;
import com.dannycode.chatApp.dto.ChatRoomDTO;
import com.dannycode.chatApp.mapper.ChatRoomMapper;
import com.dannycode.chatApp.model.ChatRoom;
import com.dannycode.chatApp.model.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepo chatRoomRepo;
    private final UserRepo userRepo;

    // Create a new chat room
    public ChatRoomDTO createRoom(String roomName, List<String> usernames) {
        if (chatRoomRepo.findByName(roomName).isPresent()) {
            throw new RuntimeException("Chat room already exists");
        }

        List<User> users = userRepo.findAllByUsernameIn(usernames);

        ChatRoom room = ChatRoom.builder()
                .name(roomName)
                .build();

        // Add users to the room
        room.getParticipants().addAll(users);

        ChatRoom savedRoom = chatRoomRepo.save(room);
        return ChatRoomMapper.toDTO(savedRoom);
    }

    // Get all chat rooms
    public List<ChatRoomDTO> getAllRooms() {
        return chatRoomRepo.findAll()
                .stream()
                .map(ChatRoomMapper::toDTO)
                .toList();
    }

    // Find chat room by name
    public ChatRoomDTO findByName(String roomName) {
        ChatRoom room = chatRoomRepo.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return ChatRoomMapper.toDTO(room);
    }

    // Internal method: add a user to a room (returns entity)
    private ChatRoom addUserToRoomEntity(String roomName, String username) {
        ChatRoom room = chatRoomRepo.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!room.getParticipants().contains(user)) {
            room.getParticipants().add(user);
        }

        return chatRoomRepo.save(room);
    }

    // User joins a room
    public ChatRoomDTO joinRoom(String roomName, String username) {
        ChatRoom room = addUserToRoomEntity(roomName, username);
        return ChatRoomMapper.toDTO(room);
    }

    // Admin adds a user to a room
    public ChatRoomDTO addUserToRoom(String roomName, String username) {
        ChatRoom room = addUserToRoomEntity(roomName, username);
        return ChatRoomMapper.toDTO(room);
    }
}