package com.relex.messenger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "confirmation_token")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    private User user;

    private LocalDateTime expiresAt;

    public ConfirmationToken() {

    }

    public ConfirmationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }
}

