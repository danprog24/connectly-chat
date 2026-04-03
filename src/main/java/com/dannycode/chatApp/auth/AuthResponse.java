package com.dannycode.chatApp.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthResponse {

    private String token;

    private String message;
}
