package com.relex.messenger.entity;

import com.relex.messenger.enums.ChatStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_chat")
public class UserChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "left_time")
    private LocalDateTime leftTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChatStatus status;

    public UserChat() {

    }

    public UserChat(User user, Chat chat, ChatStatus status) {
        this.user = user;
        this.chat = chat;
        this.status = status;
    }
}