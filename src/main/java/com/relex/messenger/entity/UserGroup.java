package com.relex.messenger.entity;

import com.relex.messenger.enums.GroupStatus;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "user_group")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
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