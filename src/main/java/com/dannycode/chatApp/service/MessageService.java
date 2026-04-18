// package com.dannycode.chatApp.service;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.dannycode.chatApp.model.ChatRoom;
// import com.dannycode.chatApp.model.Message;
// import com.dannycode.chatApp.model.User;
// import com.dannycode.chatApp.repository.ChatRoomRepo;
// import com.dannycode.chatApp.repository.MessageRepo;
// import com.dannycode.chatApp.repository.UserRepo;

// import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
// @Service
// public class MessageService {

//     private final ChatRoomRepo chatRoomRepo;
//     private final UserRepo userRepo;
//     private final MessageRepo messageRepo;

//     // helper — get or create room
//     private ChatRoom getOrCreateRoom(String roomName) {
//         return chatRoomRepo.findByName(roomName)
//             .orElseGet(() -> {
//                 ChatRoom newRoom = ChatRoom.builder()
//                     .name(roomName)
//                     .build();
//                 return chatRoomRepo.save(newRoom);
//             });
//     }

//     public Message sendMessage(String roomName, String username, String content) {
//         ChatRoom room = getOrCreateRoom(roomName); // ✅ create if not exists

//         User sender = userRepo.findByUsername(username)
//             .orElseThrow(() -> new RuntimeException("User not found: " + username));

//         Message message = Message.builder()
//             .chatRoom(room)
//             .sender(sender)
//             .content(content)
//             .timestamp(LocalDateTime.now())
//             .build();

//         return messageRepo.save(message);
//     }

//     public List<Message> getMessagesByRoom(String roomName) {
//         ChatRoom room = getOrCreateRoom(roomName); // ✅ create if not exists
//         return messageRepo.findByChatRoomOrderByTimestampAsc(room);
//     }


//     public int getUnreadCount(String username) {
//         User user = userRepo.findByUsername(username)
//                 .orElseThrow(() -> new RuntimeException("User not found: " + username));

//         List<String> roomNames = user.getFriends().stream()
//                 .map(friend -> {
//                     List<String> names = List.of(user.getUsername(), friend.getUsername());
//                     return names.stream().sorted().reduce((a, b) -> a + "_" + b)
//                             .orElseThrow(() -> new RuntimeException("Failed to generate room name"));
//                 })
//                 .collect(Collectors.toList());

//         if (roomNames.isEmpty()) return 0;

//         return messageRepo.countByChatRoom_NameInAndSender_UsernameNotAndReadFalse(
//             roomNames,
//             username
//         );
//     }

//     @Transactional
//     public void markMessagesAsRead(String roomName, String username) {
//         messageRepo.markMessagesAsRead(roomName, username);
//     }

// }







package com.dannycode.chatApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dannycode.chatApp.cache.MessageCacheService;
import com.dannycode.chatApp.model.ChatRoom;
import com.dannycode.chatApp.model.Message;
import com.dannycode.chatApp.model.User;
import com.dannycode.chatApp.repository.ChatRoomRepo;
import com.dannycode.chatApp.repository.MessageRepo;
import com.dannycode.chatApp.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final ChatRoomRepo       chatRoomRepo;
    private final UserRepo           userRepo;
    private final MessageRepo        messageRepo;
    private final MessageCacheService messageCache;   // ← injected by Spring


    private ChatRoom getOrCreateRoom(String roomName) {
        return chatRoomRepo.findByName(roomName)
            .orElseGet(() -> {
                ChatRoom newRoom = ChatRoom.builder()
                    .name(roomName)
                    .build();
                return chatRoomRepo.save(newRoom);
            });
    }

    // -------------------------------------------------------------------------
    // Send message
    // -------------------------------------------------------------------------

    /**
     * Persists a new message and appends it to the LRU cache so the next
     * {@link #getMessagesByRoom} call is served from memory.
     */
    public Message sendMessage(String roomName, String username, String content) {
        ChatRoom room = getOrCreateRoom(roomName);

        User sender = userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Message message = Message.builder()
            .chatRoom(room)
            .sender(sender)
            .content(content)
            .timestamp(LocalDateTime.now())
            .build();

        Message saved = messageRepo.save(message);

        // Update cache in-place — no need to reload from DB
        messageCache.append(roomName, saved);

        return saved;
    }

    // -------------------------------------------------------------------------
    // Get messages  (cache-first)
    // -------------------------------------------------------------------------

    
    // Returns messages for a room
    public List<Message> getMessagesByRoom(String roomName) {
        // 1. Cache hit?
        List<Message> cached = messageCache.get(roomName);
        if (cached != null) {
            return cached;
        }

        // 2. Cache miss — load from DB
        ChatRoom room     = getOrCreateRoom(roomName);
        List<Message> messages = messageRepo.findByChatRoomOrderByTimestampAsc(room);

        // 3. Populate cache for next call
        messageCache.put(roomName, messages);

        return messages;
    }

    // -------------------------------------------------------------------------
    // Unread count  (not cached — counts change frequently, cache would go stale)
    // -------------------------------------------------------------------------

    public int getUnreadCount(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<String> roomNames = user.getFriends().stream()
                .map(friend -> {
                    List<String> names = List.of(user.getUsername(), friend.getUsername());
                    return names.stream().sorted().reduce((a, b) -> a + "_" + b)
                            .orElseThrow(() -> new RuntimeException("Failed to generate room name"));
                })
                .collect(Collectors.toList());

        if (roomNames.isEmpty()) return 0;

        return messageRepo.countByChatRoom_NameInAndSender_UsernameNotAndReadFalse(
            roomNames,
            username
        );
    }

    // Mark as read  (invalidate cache so read-status is fresh on next load)

    /**
     * Marks messages as read in the DB, then evicts the room from the cache
     * so the next fetch reflects the updated read status.
     */
    @Transactional
    public void markMessagesAsRead(String roomName, String username) {
        messageRepo.markMessagesAsRead(roomName, username);

        // Evict so the next getMessagesByRoom reloads with correct read flags
        messageCache.evict(roomName);
    }
}
