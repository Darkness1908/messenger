package com.relex.messenger.entity;

import com.relex.messenger.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_user")
public class UserUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "first_id")
    private User firstUser;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "second_id")
    private User secondUser;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
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