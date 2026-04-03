package com.dannycode.chatApp.controller;

import com.dannycode.chatApp.auth.AddUserRequest;
import com.dannycode.chatApp.auth.CreateChatRoomRequest;
import com.dannycode.chatApp.dto.ChatRoomDTO;
import com.dannycode.chatApp.dto.JoinRoomRequest;
import com.dannycode.chatApp.service.ChatRoomService;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chatrooms")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // Create a new chat room
    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody CreateChatRoomRequest request) {
        return ResponseEntity.ok(chatRoomService.createRoom(request.roomName(), request.usernames()));
    }

    // User joins a chat room
    @PostMapping("/{roomName}/join")
    public ResponseEntity<ChatRoomDTO> joinRoom(
            @PathVariable String roomName,
            @RequestBody JoinRoomRequest request
    ) {
        return ResponseEntity.ok(chatRoomService.joinRoom(roomName, request.getUsername()));
    }

    // Admin adds a user to a chat room
    @PostMapping("/{roomName}/addUser")
    public ResponseEntity<ChatRoomDTO> addUserToRoom(
            @PathVariable String roomName,
            @RequestBody AddUserRequest request
    ) {
        return ResponseEntity.ok(chatRoomService.addUserToRoom(roomName, request.getUsername()));
    }

    // Search chat room by name
    @GetMapping("/{roomName}")
    public ResponseEntity<ChatRoomDTO> getRoomByName(@PathVariable String roomName) {
        return ResponseEntity.ok(chatRoomService.findByName(roomName));
    }

    // Get all chat rooms
    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllRooms() {
        return ResponseEntity.ok(chatRoomService.getAllRooms());
    }
}