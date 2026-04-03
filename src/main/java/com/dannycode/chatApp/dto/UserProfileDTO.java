package com.dannycode.chatApp.dto;


import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private boolean online;
    private LocalDateTime createdAt;
    private int mutualChatRoomsCount;
}
