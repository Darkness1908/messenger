package com.relex.messenger.dto;

import com.relex.messenger.entity.User;
import org.jetbrains.annotations.NotNull;

public record ExtendedUserInfo (
        Long id,
        String name,
        String surname,
        String patronymic,
        String username,
        String email,
        String phoneNumber
) {
    public ExtendedUserInfo(@NotNull User user) {
        this(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getPatronymic(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }
}

