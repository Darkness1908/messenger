package com.relex.messenger.repository;

import com.relex.messenger.entity.Chat;
import com.relex.messenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    boolean existsByIdAndAdministrator(Long chatId, User user);
}