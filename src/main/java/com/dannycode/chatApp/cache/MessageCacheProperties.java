package com.dannycode.chatApp.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "chat.cache")
public class MessageCacheProperties {

    // Maximum number of chat rooms to keep cached simultaneously. Default: 200 
    private int capacity = 200;

    // Maximum number of messages stored per room. Default: 100 
    private int messagesPerRoom = 100;
}
