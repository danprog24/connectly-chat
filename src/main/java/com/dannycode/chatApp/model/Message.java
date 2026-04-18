package com.dannycode.chatApp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    @JsonIgnore // 
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({"password", "friends", "chatrooms"}) // Prevent sender details from being serialized in responses
    private User sender;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean read = false; // track if message has been read

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
