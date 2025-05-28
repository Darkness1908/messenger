package com.relex.messenger.repository;

import com.relex.messenger.entity.ConfirmationToken;
import com.relex.messenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    boolean existsByUser(User user);

    @Query("SELECT ct.user FROM ConfirmationToken ct WHERE ct.token = :token")
    User getUser(@Param("token") String token);

    Optional<ConfirmationToken> findByToken(String token);

    void deleteAllByExpiresAtBefore(LocalDateTime threshold);
}
