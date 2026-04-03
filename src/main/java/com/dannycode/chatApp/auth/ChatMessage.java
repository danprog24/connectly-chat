package com.dannycode.chatApp.auth;

import lombok.Data;

@Data
public class ChatMessage {

    private String content;
    private String sender;
    private String receiver;
    private MessageType messageType;
    private String time;

}
