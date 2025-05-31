package com.relex.messenger.controller;

import com.relex.messenger.dto.NotificationInfo;
import com.relex.messenger.entity.User;
import com.relex.messenger.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/")
    public ResponseEntity<?> getNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<NotificationInfo> notificationInfos = notificationService.showNotifications(user.getId());
        if (notificationInfos.isEmpty()) {
            return ResponseEntity.ok("No notifications");
        }
        return ResponseEntity.ok(notificationInfos);
    } //checked

    @DeleteMapping("/{invitationId}")
    public ResponseEntity<?> declineInvitation(@PathVariable Long invitationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User notified = (User) authentication.getPrincipal();

        notificationService.declineInvitation(invitationId, notified);
        return ResponseEntity.ok("You declined this invitation");
    } //checked

    @PostMapping("/{invitationId}")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long invitationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User notified = (User) authentication.getPrincipal();

        notificationService.acceptInvitation(invitationId, notified);
        return ResponseEntity.ok("You accept this invitation");
    } //checked
}
