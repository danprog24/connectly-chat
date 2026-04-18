// package com.dannycode.chatApp.controller;

// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.messaging.simp.SimpMessagingTemplate;

// import lombok.RequiredArgsConstructor;

// import com.dannycode.chatApp.auth.ChatMessage;
// import com.dannycode.chatApp.auth.StatusUpdate;
// import com.dannycode.chatApp.service.MessageService;
// import com.dannycode.chatApp.service.UserService;

// import java.security.Principal;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// @RestController
// @CrossOrigin(origins = {"http://localhost:5173", "https://connectly-chatz-nave.vercel.app"})
// @RequiredArgsConstructor
// public class ChatController {

//     private final SimpMessagingTemplate messagingTemplate;
//     private final MessageService messageService;
//     private final UserService userService;

//    @MessageMapping("/user.online")
//     public void userOnline(Principal principal) {
//         System.out.println("=== user.online called, principal: " + principal);
//         if (principal == null) return;
//         System.out.println("=== Setting online: " + principal.getName());
//         userService.setOnlineStatus(principal.getName(), true);
//         messagingTemplate.convertAndSend("/topic/status",
//             new StatusUpdate(principal.getName(), true));
//     }

//     @MessageMapping("/user.offline")
//     public void userOffline(Principal principal) {
//         if (principal == null) return; // null check
//         userService.setOnlineStatus(principal.getName(), false);
//         messagingTemplate.convertAndSend("/topic/status",
//             new StatusUpdate(principal.getName(), false));
//     }

//     @MessageMapping("/chat.sendMessage")
//     @SendTo("/topic/public")
//     public ChatMessage sendMessage(ChatMessage chatMessage) {
//         return chatMessage;
//     }

//     @MessageMapping("/chat.privateMessage")
//     public void sendPrivateMessage(ChatMessage chatMessage) {
//         String roomName = Stream.of(chatMessage.getSender(), chatMessage.getReceiver())
//                 .sorted()
//                 .collect(Collectors.joining("-"));

//         messageService.sendMessage(roomName, chatMessage.getSender(), chatMessage.getContent());

//         messagingTemplate.convertAndSendToUser(chatMessage.getReceiver(), "/queue/messages", chatMessage);
//         messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/messages", chatMessage);
//     }

//     @MessageMapping("/chat.addUser")
//     @SendTo("/topic/public")
//     public ChatMessage addUser(ChatMessage chatMessage) {
//         chatMessage.setContent(chatMessage.getSender() + " joined the chat");
//         return chatMessage;
//     }
// }






package com.dannycode.chatApp.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.RequiredArgsConstructor;

import com.dannycode.chatApp.auth.ChatMessage;
import com.dannycode.chatApp.auth.StatusUpdate;
import com.dannycode.chatApp.service.MessageService;
import com.dannycode.chatApp.service.UserService;

import java.security.Principal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "https://connectly-chatz-nave.vercel.app"})
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;

    @MessageMapping("/user.online")
    public void userOnline(Principal principal) {
        if (principal == null) return;
        try {
            System.out.println("=== Setting online: " + principal.getName());
            userService.setOnlineStatus(principal.getName(), true);
            messagingTemplate.convertAndSend("/topic/status",
                new StatusUpdate(principal.getName(), true));
        } catch (Exception e) {
            System.out.println("=== userOnline error: " + e.getMessage());
        }
    }

    @MessageMapping("/user.offline")
    public void userOffline(Principal principal) {
        if (principal == null) return;
        try {
            userService.setOnlineStatus(principal.getName(), false);
            messagingTemplate.convertAndSend("/topic/status",
                new StatusUpdate(principal.getName(), false));
        } catch (Exception e) {
            System.out.println("=== userOffline error: " + e.getMessage());
        }
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(ChatMessage chatMessage) {
        String roomName = Stream.of(chatMessage.getSender(), chatMessage.getReceiver())
                .sorted()
                .collect(Collectors.joining("-"));

        messageService.sendMessage(roomName, chatMessage.getSender(), chatMessage.getContent());

        messagingTemplate.convertAndSendToUser(chatMessage.getReceiver(), "/queue/messages", chatMessage);
        messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/messages", chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + " joined the chat");
        return chatMessage;
    }
}
