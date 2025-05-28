package com.relex.messenger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "message")
    private String content;

    @Setter
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "time")
    private LocalDateTime time;

    @Setter
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Setter
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
