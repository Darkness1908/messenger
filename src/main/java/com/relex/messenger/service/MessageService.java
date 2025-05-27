package com.relex.messenger.service;

import com.relex.messenger.entity.*;
import com.relex.messenger.enums.ChatStatus;
import com.relex.messenger.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserChatRepository userChatRepository;

    public void editMessage(Long messageId, User editor, String content) {
        Message editingMessage = getMessage(messageId);
        Chat chat = editingMessage.getChat();

        if (userIsNotInChat(editor, chat)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not a member of this chat");
        }

        if (!editingMessage.getSender().equals(editor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to edit this message");
        }


        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        if (editingMessage.getTime().isBefore(oneDayAgo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can't edit messages after 24 hours of sending");
        }

        editingMessage.setContent(content);
        messageRepository.save(editingMessage);
    }

    @Transactional
    public void deleteMessage(Long messageId, User user) {
        Message delitingMessage = getMessage(messageId);
        Chat chat = delitingMessage.getChat();

        if (userIsNotInChat(user, chat)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not a member of this chat");
        }

        if (!delitingMessage.getSender().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this message");
        }

        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        if (delitingMessage.getTime().isBefore(oneDayAgo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can't delete messages after 24 hours of sending");
        }

        chat.setLastMessageTime(LocalDateTime.now());
        chat.setNumberOfMessages(chat.getNumberOfMessages() - 1L);
        messageRepository.delete(delitingMessage);
    }

    private @NotNull Message getMessage(Long id) {
        return messageRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Message does not exist"));
    }

    private boolean userIsNotInChat(@NotNull User user, @NotNull Chat chat) {
        return !userChatRepository.existsByUserIdAndChatIdAndStatus(user.getId(), chat.getId(), ChatStatus.JOINED);
    }
}