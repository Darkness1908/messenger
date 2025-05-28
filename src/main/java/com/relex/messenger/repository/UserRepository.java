package com.relex.messenger.repository;

import com.relex.messenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    void deleteAllByDeletedAtBefore(LocalDateTime threshold);

    Optional<User> findByUsername(String username);
}


