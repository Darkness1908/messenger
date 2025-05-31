package com.relex.messenger.entity;

import com.relex.messenger.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_user")
public class UserUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_id")
    private User firstUser;

    @ManyToOne
    @JoinColumn(name = "second_id")
    private User secondUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus userStatus;

    public UserUser() {

    }

    public UserUser(User notified, User inviter, UserStatus userStatus) {
        firstUser = inviter;
        secondUser = notified;
        this.userStatus = userStatus;
    }
}