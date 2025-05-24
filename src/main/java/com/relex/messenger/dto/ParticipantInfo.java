package com.relex.messenger.dto;


import com.relex.messenger.entity.User;
import org.jetbrains.annotations.NotNull;

public record ParticipantInfo (
    Long userId,
    String name
) {
    public ParticipantInfo (@NotNull User participant) {
        this(
                participant.getId(),
                participant.getName() + " " + participant.getSurname()
        );
    }
}
