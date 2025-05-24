package com.relex.messenger.dto;

import com.relex.messenger.entity.Notification;
import com.relex.messenger.enums.NotificationType;
import org.jetbrains.annotations.NotNull;

public record NotificationInfo (
        Long notificationId,
        Long groupId,
        Long chatId,
        String senderName,
        NotificationType type
) {
    public NotificationInfo(@NotNull Notification notification) {
        this (
                notification.getId(),
                notification.getGroup() == null ? null : notification.getGroup().getId(),
                notification.getChat() == null ? null : notification.getChat().getId(),
                notification.getSender().getName(),
                notification.getType()
        );
    }
}
