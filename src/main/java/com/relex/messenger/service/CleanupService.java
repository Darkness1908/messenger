package com.relex.messenger.service;

import com.relex.messenger.repository.ConfirmationTokenRepository;
import com.relex.messenger.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class CleanupService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 0 12 * * *")
    public void removeOldDeletedRecords() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        userRepository.deleteAllByDeletedAtBefore(threshold);
    }

    @Transactional
    @Scheduled(cron = "* 1/2 * * * *")
    public void removeExpiredConfirmationToken() {
        LocalDateTime threshold = LocalDateTime.now();
        confirmationTokenRepository.deleteAllByExpiresAtBefore(threshold);
    }
}