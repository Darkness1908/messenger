package com.relex.messenger.entity;

import com.relex.messenger.dto.GroupForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Getter
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "number_of_participants")
    private Long numberOfParticipants;

    @Setter
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