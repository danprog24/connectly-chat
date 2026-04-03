package com.dannycode.chatApp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonIgnoreProperties({"email", "password", "chatRooms", "friends"}) // Avoid circular reference and sensitive info
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    @JsonIgnoreProperties({"email", "password", "chatRooms", "friends"}) // Avoid circular reference and sensitive info
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status; // PENDING, ACCEPTED, REJECTED
    
}
