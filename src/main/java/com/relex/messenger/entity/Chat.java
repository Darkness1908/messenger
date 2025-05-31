package com.relex.messenger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "administrator_id")
    private User administrator;

    @Column
    private LocalDateTime lastMessageTime;

    @Column
    private Long numberOfMessages;

    private Long numberOfParticipants;

    public Chat() {

    }

    public Chat(String name, User administrator) {
        this.name = name;
        this.administrator = administrator;
        this.lastMessageTime = LocalDateTime.now();
        this.numberOfMessages = 1L;
        this.numberOfParticipants = 0L;
    }
}
