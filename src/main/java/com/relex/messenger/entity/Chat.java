package com.relex.messenger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name")
    private String name;

    @Setter
    @ManyToOne
    @JoinColumn(name = "administrator_id")
    private User administrator;

    @Setter
    @Column
    private LocalDateTime lastMessageTime;

    @Setter
    @Column
    private Long numberOfMessages;

    @Setter
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
