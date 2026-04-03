package com.dannycode.chatApp.repository;

import com.dannycode.chatApp.model.Message;
import com.dannycode.chatApp.model.ChatRoom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

}
