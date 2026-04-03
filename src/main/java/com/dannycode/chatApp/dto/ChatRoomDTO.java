package com.dannycode.chatApp.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    
    private Long id;
    private String roomName;
    private int participantCount; 
    private Set<UserDTO> participantUsernames; // shows who is in the room

}
