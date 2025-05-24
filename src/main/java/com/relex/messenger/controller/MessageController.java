package com.relex.messenger.controller;

import com.relex.messenger.dto.MessageForm;
import com.relex.messenger.entity.User;
import com.relex.messenger.service.MessageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageForm messageForm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User sender = (User) authentication.getPrincipal();

        messageService.sendMessage(sender, messageForm);
        return ResponseEntity.ok("Message sent");
    } //checked

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
