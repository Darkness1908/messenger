package com.relex.messenger.repository;

import com.relex.messenger.entity.Chat;
import com.relex.messenger.entity.User;
import com.relex.messenger.entity.UserChat;
import com.relex.messenger.enums.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatRepository extends JpaRepository<UserChat, Long> {

    @Query("SELECT uc.chat FROM UserChat uc WHERE uc.user.id = :userId AND uc.status = :chatStatus")
    List<Chat> findChatsByUserIdAndStatus(@Param("userId") Long userId,
                                          @Param("chatStatus") ChatStatus chatStatus);

    @Query("SELECT uc.user FROM UserChat uc WHERE uc.chat.id = :chatId AND uc.status = :chatStatus")
    List<User> findUsersByChatIdAndStatus(@Param("chatId") Long ChatId,
                                          @Param("chatStatus") ChatStatus chatStatus);

    boolean existsByUserIdAndChatIdAndStatus(Long userId, Long groupId, ChatStatus status);

    UserChat getByUserIdAndChatId(Long userId, Long chatId);

    List<UserChat> findByChat(Chat chat);
}
