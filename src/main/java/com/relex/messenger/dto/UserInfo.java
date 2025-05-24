package com.relex.messenger.dto;

import com.relex.messenger.entity.User;
import org.jetbrains.annotations.NotNull;

public record UserInfo(
        Long id,
        String name,
        String surname,
        String patronymic,
        String username
) {
    public UserInfo(@NotNull User user) {
        this(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getPatronymic(),
                user.getUsername()
        );
    }
}
