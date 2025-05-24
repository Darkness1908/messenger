package com.relex.messenger.dto;

import com.relex.messenger.entity.Message;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public record MessageInfo(
        String content,
        LocalDateTime time,
        String senderName,
        Long chatId
) {
    public MessageInfo (@NotNull Message message) {
        this(
                message.getContent(),
                message.getTime(),
                message.getSender().getName() + " " + message.getSender().getSurname(),
                message.getChat().getId()
        );
    }
}
