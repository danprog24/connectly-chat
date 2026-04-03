package com.dannycode.chatApp.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.dannycode.chatApp.dto.ChatRoomDTO;
import com.dannycode.chatApp.dto.UserDTO;
import com.dannycode.chatApp.model.ChatRoom;

public class ChatRoomMapper {

    public static ChatRoomDTO toDTO(ChatRoom room) {
        Set<UserDTO> participantsDTO = room.getParticipants()
                .stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());

        return new ChatRoomDTO(
                room.getId(),
                room.getName(),
                participantsDTO.size(),
                participantsDTO
        );
    }
}   