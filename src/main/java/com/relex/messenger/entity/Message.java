package com.relex.messenger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "time")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToMany
    @JoinTable(
            name = "hidden_message_for_user",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> notDisplayFor;

    public Message () {

    }

    public Message (User sender, Chat chat, String content) {
        this.sender = sender;
        this.chat = chat;
        this.content = content;
        time = LocalDateTime.now();
    }
}
