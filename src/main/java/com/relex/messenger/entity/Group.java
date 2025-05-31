package com.relex.messenger.entity;

import com.relex.messenger.dto.GroupForm;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "number_of_participants")
    private Long numberOfParticipants;

    @ManyToOne
    @JoinColumn(name = "administrator_id")
    private User administrator;

    public Group() {

    }

    public Group(GroupForm groupForm, User creator) {
        name = groupForm.groupName();
        description = groupForm.description();
        administrator = creator;
        numberOfParticipants = 1L;
    }
}