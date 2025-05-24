package com.relex.messenger.entity;

import com.relex.messenger.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "notified_id")
    private User notified;

    @Setter
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Setter
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Setter
    @ManyToOne
    @JoinColumn(name = "inviter_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(name = "type")
    private NotificationType type;

    public Notification() {

    }

    public Notification(User notified, User inviter, NotificationType type, Group group) {
        this.notified = notified;
        this.sender = inviter;
        this.type = type;
        this.group = group;
    }

    public Notification(User notified, User inviter, NotificationType type, Chat chat) {
        this.notified = notified;
        this.sender = inviter;
        this.type = type;
        this.chat = chat;
    }

    public Notification(User notified, User sender, NotificationType type) {
        this.notified = notified;
        this.sender = sender;
        this.type = type;
    }
}
