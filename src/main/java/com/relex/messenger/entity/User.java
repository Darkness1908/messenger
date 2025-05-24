package com.relex.messenger.entity;

import com.relex.messenger.dto.ProfileUpdateForm;
import com.relex.messenger.dto.RegistrationForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_activated")
    private boolean isActivated;

    public void updateUser(@NotNull ProfileUpdateForm profile) {
        setName(profile.name());
        setSurname(profile.surname());
        setPatronymic(profile.patronymic());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public User() {

    }

    public User(RegistrationForm registrationForm, String hashPassword) {
        username = registrationForm.username();
        email = registrationForm.email();
        phoneNumber = registrationForm.phoneNumber();
        password = hashPassword;
        name = registrationForm.name();
        surname = registrationForm.surname();
        patronymic = registrationForm.patronymic();
        isActivated = false;
    }
}