package com.relex.messenger.service;

import com.relex.messenger.dto.ChatInfo;
import com.relex.messenger.dto.MessageInfo;
import com.relex.messenger.dto.ParticipantInfo;
import com.relex.messenger.entity.*;
import com.relex.messenger.enums.ChatStatus;
import com.relex.messenger.enums.UserStatus;
import com.relex.messenger.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserUserRepository userUserRepository;

    @Transactional
    public void createChat(String chatName, User creator) {
        Chat chat = new Chat(chatName, creator);
        UserChat userChat = new UserChat(creator, chat, ChatStatus.JOINED);
        userChatRepository.save(userChat);
        chatRepository.save(chat);
    }

    @Transactional
    public void cleanChat(Long chatId, User user) {
        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Chat not found");
        }

        if (!isMember(user.getId(), chatId) && !hasLeft(user.getId(), chatId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't clean a chat history of a chat you have never been a member of");
        }

        List<Message> messages = messageRepository.findByChatId(chatId);
        for (Message message : messages) {
            message.getNotDisplayFor().add(user);
        }
    }

    @Transactional
    public void leaveChat(Long chatId, User user) {
        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Chat not found");
        }

        if (hasLeft(user.getId(), chatId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You already left from the chat");
        }

        if (!isMember(user.getId(), chatId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You can't leave a chat you are not member of");
        }

        UserChat userChat = userChatRepository.getByUserIdAndChatId(user.getId(), chatId);
        userChat.setStatus(ChatStatus.LEFT);
        userChat.setLeftTime(LocalDateTime.now());
        Chat chat = chatRepository.getReferenceById(chatId);
        chat.setNumberOfParticipants(chat.getNumberOfParticipants() - 1L);
        userChatRepository.save(userChat);
    }

    @Transactional
    public void addUserToChat(Long chatId, Long addingUserId, User user) {
        if (!userRepository.existsById(addingUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Chat not found");
        }

        if (!isMember(user.getId(), chatId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not a member of this chat");
        }

        if (user.getId().equals(addingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't add yourself");
        }

        if (isMember(addingUserId, chatId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You can't add a user to a chat they are already a member of");
        }

        if (hasLeft(addingUserId, chatId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You can't add a user who already has left the chat");
        }

        if (!isFriend(addingUserId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can invite only friends to a chat");
        }

        User addingUser = userRepository.getReferenceById(addingUserId);
        Chat chat = chatRepository.getReferenceById(chatId);
        chat.setNumberOfParticipants(chat.getNumberOfParticipants() + 1L);
        UserChat userChat = new UserChat(addingUser, chat, ChatStatus.JOINED);
        userChatRepository.save(userChat);
    }

    @Transactional
    public void kickUserFromChat(Long chatId, Long kickingUserId, User admin) {
        if (!userRepository.existsById(kickingUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found");
        }

        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Chat not found");
        }

        if (isUserNotAdministrator(admin, chatId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not an administrator of this chat");
        }

        if (admin.getId().equals(kickingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't kick yourself");
        }

        if (!isMember(kickingUserId, chatId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You can't kick the user from a chat they are not a member of");
        }

        UserChat userChat = userChatRepository.getByUserIdAndChatId(kickingUserId, chatId);
        userChat.setStatus(ChatStatus.LEFT);
        userChat.setLeftTime(LocalDateTime.now());
        Chat chat = chatRepository.getReferenceById(chatId);
        chat.setNumberOfParticipants(chat.getNumberOfParticipants() - 1L);
        userChatRepository.save(userChat);
    }

    public void changeChatName(@NotBlank String name, Long chatId, User admin) {
        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Chat not found");
        }

        if (isUserNotAdministrator(admin, chatId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not an administrator of this chat");
        }

        Chat chat = chatRepository.getReferenceById(chatId);
        chat.setName(name);
        chatRepository.save(chat);
    }

    public List<ChatInfo> getMyChats(Long userId) {
        List<Chat> chats = userChatRepository.findChatsByUserIdAndStatus(userId, ChatStatus.JOINED);
        chats.sort(Comparator.comparing(Chat::getLastMessageTime).reversed());
        return chatsToDto(chats);
    }

    public List<MessageInfo> getMessages(User user, Long chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Chat not found");
        }

        if (!isMember(user.getId(), chatId) && !hasLeft(user.getId(), chatId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can't check a chat history of a chat you have never been a member of");
        }

        UserChat userChat = userChatRepository.getByUserIdAndChatId(user.getId(), chatId);
        List<Message> messages = messageRepository.getMessages(chatId,
                user, userChat.getLeftTime());
        messages.sort(Comparator.comparing(Message::getTime).reversed());
        return messagesToDto(messages);
    }

    public List<ParticipantInfo> getChatParticipants(Long chatId, User user) {
        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Chat not found");
        }

        if (!isMember(user.getId(), chatId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can't view the participants of a chat you are not a member of");
        }

        List<User> participants = userChatRepository.findUsersByChatIdAndStatus(chatId, ChatStatus.JOINED);
        return participantsToDto(participants);
    }

    private List<ChatInfo> chatsToDto(@NotNull List<Chat> chats) {
        return chats.stream()
                .map(ChatInfo::new)
                .collect(Collectors.toList());
    }

    private List<MessageInfo> messagesToDto(@NotNull List<Message> messages) {
        return messages.stream()
                .map(MessageInfo::new)
                .collect(Collectors.toList());
    }

    private List<ParticipantInfo> participantsToDto(@NotNull List<User> participants) {
        return participants.stream()
                .map(ParticipantInfo::new)
                .collect(Collectors.toList());
    }

    private boolean hasLeft(Long userId, Long chatId) {
        return userChatRepository.existsByUserIdAndChatIdAndStatus(userId, chatId, ChatStatus.LEFT);
    }

    private boolean isMember(Long userId, Long chatId) {
        return userChatRepository.existsByUserIdAndChatIdAndStatus(userId, chatId, ChatStatus.JOINED);
    }

    private boolean isFriend(Long firstUserId, Long secondUserId) {
        return userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                firstUserId, secondUserId, UserStatus.FRIEND) ||
                userUserRepository.existsByFirstUserIdAndSecondUserIdAndUserStatus(
                        secondUserId, firstUserId, UserStatus.FRIEND);
    }

    private boolean isUserNotAdministrator(User user, Long chatId) {
        return !chatRepository.existsByIdAndAdministrator(chatId, user);
    }
}
