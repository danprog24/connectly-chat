package com.dannycode.chatApp.repository;

import com.dannycode.chatApp.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatRoomRepo extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByName(String name);
}
