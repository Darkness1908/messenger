package com.relex.messenger.repository;

import com.relex.messenger.entity.Message;
import com.relex.messenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId);

    @Query("SELECT m FROM Message m" +
            " WHERE m.chat.id = :chatId " +
            " AND :user NOT MEMBER OF m.notDisplayFor " +
            "AND m.time < COALESCE(:leftTime, CURRENT_TIMESTAMP)")
    List<Message> getMessages(@Param("chatId") Long chatId,
                                            @Param("user") User user, @Param("leftTime") LocalDateTime leftTime);
}
