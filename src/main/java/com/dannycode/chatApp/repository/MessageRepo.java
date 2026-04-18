package com.dannycode.chatApp.repository;

import com.dannycode.chatApp.model.Message;
import com.dannycode.chatApp.model.ChatRoom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomOrderByTimestampAsc(
        ChatRoom chatRoom
    );

    int countByChatRoom_NameInAndSender_UsernameNotAndReadFalse(
            List<String> roomNames,
            String usernames
    );

    // Optional: to exclude multiple usernames
    int countByChatRoom_NameInAndSender_UsernameNotInAndReadFalse(
            List<String> roomNames,
            List<String> usernames
    );

    @Modifying
    @Query("UPDATE Message m SET m.read = true " +
        "WHERE m.chatRoom.name = :roomName " +
        "AND m.sender.username <> :username " +
        "AND m.read = false")
    void markMessagesAsRead(String roomName, String username);

}
