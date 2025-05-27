package com.relex.messenger.controller;

import com.relex.messenger.entity.User;
import com.relex.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PatchMapping("/{messageId}")
    public ResponseEntity<?> editMessage(@PathVariable Long messageId,
                                         @RequestParam String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User editor = (User) authentication.getPrincipal();

        messageService.editMessage(messageId, editor, content);
        return ResponseEntity.ok("Message edited");
    } //checked

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        messageService.deleteMessage(messageId, user);
        return ResponseEntity.ok("Message deleted");
    } //checked
}
