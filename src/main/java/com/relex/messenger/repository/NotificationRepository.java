package com.relex.messenger.repository;

import com.relex.messenger.entity.Chat;
import com.relex.messenger.entity.Notification;
import com.relex.messenger.entity.User;
import com.relex.messenger.entity.UserGroup;
import com.relex.messenger.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByNotifiedIdAndChatIdAndType(Long senderId, Long chatId, NotificationType type);

    boolean existsByNotifiedIdAndGroupId(Long userId, Long groupId);

    List<Notification> findByNotifiedId(Long userId);

    boolean existsByIdAndNotified(Long notificationId, User notified);

    boolean existsByNotifiedIdAndSenderIdAndType(Long userId, Long inviterId, NotificationType notificationType);

    boolean existsByNotifiedAndChat(User participant, Chat chat);

    Notification findByNotifiedAndChat(User participant, Chat chat);

    void deleteAllByNotifiedIdAndType(Long userId, NotificationType notificationType);

    Notification getByNotifiedIdAndGroupId(Long banningUserId, Long groupId);

    void deleteAllByNotifiedIdAndSenderIdAndTypeNot(Long blockingUserId, Long senderId, NotificationType notificationType);
}
