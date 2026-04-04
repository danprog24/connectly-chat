package com.dannycode.chatApp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dannycode.chatApp.model.Message;
import com.dannycode.chatApp.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"http://localhost:5173", "https://connectly-chatz-nave.vercel.app"})
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;


    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
        @RequestParam String roomName,
        @RequestParam String username, 
        @RequestParam String content
    ) {
        Message sentMessage = messageService.sendMessage(roomName, username, content);
        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/room/{roomName}")
    public ResponseEntity<List<Message>> getMessagesByRoom(@PathVariable String roomName) {
        java.util.List<Message> messages = messageService.getMessagesByRoom(roomName);
        return ResponseEntity.ok(messages);
    }

}
