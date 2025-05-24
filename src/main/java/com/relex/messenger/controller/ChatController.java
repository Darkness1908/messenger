package com.relex.messenger.controller;

import com.relex.messenger.dto.ChatInfo;
import com.relex.messenger.dto.ExtendedUserInfo;
import com.relex.messenger.dto.MessageInfo;
import com.relex.messenger.dto.ParticipantInfo;
import com.relex.messenger.entity.User;
import com.relex.messenger.service.ChatService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/")
    public ResponseEntity<?> createChat(@RequestBody @NotBlank String chatName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User creator = (User) authentication.getPrincipal();

        chatService.createChat(chatName, creator);
        return ResponseEntity.ok("Chat created");
    } //checked

    @PatchMapping("/{chatId}/clean")
    public ResponseEntity<?> cleanChat(@PathVariable Long chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        chatService.cleanChat(chatId, user);
        return ResponseEntity.ok("Message history has been cleaned");
    } //checked

    @PatchMapping("/{chatId}/leave")
    public ResponseEntity<?> leaveChat(@PathVariable Long chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        chatService.leaveChat(chatId, user);
        return ResponseEntity.ok("You left the chat");
    } //checked

    @PostMapping("/{chatId}/add")
    public ResponseEntity<?> addUserToChat(@PathVariable Long chatId,
                                           @RequestParam Long addingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        chatService.addUserToChat(chatId, addingUserId, user);
        return ResponseEntity.ok("You add the user to chat");
    } //checked

    @PatchMapping("/{chatId}/kick")
    public ResponseEntity<?> kickUserFromChat(@PathVariable Long chatId,
                                      @RequestParam Long kickingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();

        chatService.kickUserFromChat(chatId, kickingUserId, admin);
        return ResponseEntity.ok("You kicked the user");

    } //checked

    @PatchMapping("/{chatId}/change-name")
    public ResponseEntity<?> changeChatName(@RequestParam @NotBlank String name,
                                            @PathVariable Long chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();

        chatService.changeChatName(name, chatId, admin);
        return ResponseEntity.ok("Chat name has been changed");
    } //checked

    @GetMapping("/")
    public ResponseEntity<?> getMyChats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<ChatInfo> chatsInfo = chatService.getMyChats(user.getId());
        if (chatsInfo.isEmpty()) {
            return ResponseEntity.ok("No chats");
        }
        return ResponseEntity.ok(chatsInfo);
    } //checked

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getMessages(@PathVariable Long chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<MessageInfo> messagesInfo = chatService.getMessages(user, chatId);
        if (messagesInfo.isEmpty()) {
            return ResponseEntity.ok("No messages in this chat");
        }
        return ResponseEntity.ok(messagesInfo);
    } //checked

    @GetMapping("/{chatId}/participants")
    public ResponseEntity<?> getChatParticipants(@PathVariable Long chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<ParticipantInfo> participants = chatService.getChatParticipants(chatId, user);
        return ResponseEntity.ok(participants);
    } //checked

}
