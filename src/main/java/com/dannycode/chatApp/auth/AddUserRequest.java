package com.dannycode.chatApp.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AddUserRequest {
    
    private String username;
    
}
