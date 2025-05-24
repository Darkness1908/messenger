package com.relex.messenger.entity;

import com.relex.messenger.enums.GroupStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_group")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    @Column(name = "status")
    private GroupStatus groupStatus;

    public UserGroup() {

    }

    public UserGroup(User user, Group group, GroupStatus groupStatus) {
        this.user = user;
        this.group = group;
        this.groupStatus = groupStatus;
    }
}