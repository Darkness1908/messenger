package com.relex.messenger.dto;

import com.relex.messenger.entity.Chat;
import org.jetbrains.annotations.NotNull;

public record ChatInfo(
        String name,
        String administratorName,
        Long numberOfParticipants,
        Long numberOfMessages
) {
    public ChatInfo (@NotNull Chat chat) {
        this(
                chat.getName(),
                chat.getAdministrator().getName() + " " + chat.getAdministrator().getSurname(),
                chat.getNumberOfParticipants(),
                chat.getNumberOfMessages()
        );
    }
}
