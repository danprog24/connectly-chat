package com.dannycode.chatApp.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusUpdate {
    private String username;
    private boolean online;
}