package com.relex.messenger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "blacklisted_jwt")
public class JwtBlacklisted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @Column(name = "token")
    String token;

    @Column(name = "expires_at")
    Date expiresAt;

    public JwtBlacklisted() {

    }

    public JwtBlacklisted(String token, Date expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
