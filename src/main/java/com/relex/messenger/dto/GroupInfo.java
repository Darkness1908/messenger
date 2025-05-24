package com.relex.messenger.dto;

import com.relex.messenger.entity.Group;
import org.jetbrains.annotations.NotNull;

public record GroupInfo(
    String name,
    String description,
    Long groupId,
//    Long numberOfParticipants,
    String administratorName
) {
    public GroupInfo(@NotNull Group group) {
        this(
            group.getName(),
            group.getDescription(),
            group.getId(),
//            group.getNumberOfParticipants(),
                group.getAdministrator().getName() + " " + group.getAdministrator().getSurname()
        );
    }
}
