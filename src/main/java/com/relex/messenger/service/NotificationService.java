package com.relex.messenger.service;

import com.relex.messenger.dto.NotificationInfo;
import com.relex.messenger.entity.*;
import com.relex.messenger.enums.NotificationType;
import com.relex.messenger.enums.UserStatus;
import com.relex.messenger.repository.NotificationRepository;
import com.relex.messenger.repository.UserUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserUserRepository userUserRepository;
    private final GroupService groupService;

    public void declineInvitation(Long invitationId, User notified) {
        if (!notificationRepository.existsById(invitationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Notification not found");
        }

        Notification notification = notificationRepository.getReferenceById(invitationId);

        if (notification.getType() == NotificationType.MESSAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't decline a message");
        }

        if (isUserNotNotified(notified, invitationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can't decline someone else's invitation");
        }



        notificationRepository.delete(notification);
    }

    @Transactional
    public void acceptInvitation(Long invitationId, User notified) {
        if (!notificationRepository.existsById(invitationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Notification not found");
        }

        Notification notification = notificationRepository.getReferenceById(invitationId);

        if (notification.getType() == NotificationType.MESSAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't accept message");
        }

        if (isUserNotNotified(notified, invitationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can't accept someone else's invitation");
        }


        if (notification.getType() == NotificationType.GROUP) {
            groupService.joinGroup(notification.getGroup().getId(), notified);
        }
        else {
            UserUser userUser = new UserUser(notified, notification.getSender(), UserStatus.FRIEND);
            userUserRepository.save(userUser);
        }
        notificationRepository.delete(notification);
    }

    @Transactional
    public List<NotificationInfo> showNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByNotifiedId(userId);
        notificationRepository.deleteAllByNotifiedIdAndType(userId, NotificationType.MESSAGE);

        return notificationsToDto(notifications);
    }

    private List<NotificationInfo> notificationsToDto(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationInfo::new)
                .collect(Collectors.toList());
    }

    private boolean isUserNotNotified(User notified, Long invitationId) {
        System.out.println(invitationId + ": " + notified.getId());
        return !notificationRepository.existsByIdAndNotified(invitationId, notified);
    }

}
