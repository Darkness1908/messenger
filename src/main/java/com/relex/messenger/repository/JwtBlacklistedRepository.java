package com.relex.messenger.repository;

import com.relex.messenger.entity.JwtBlacklisted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface JwtBlacklistedRepository extends JpaRepository<JwtBlacklisted, Long> {
    boolean existsByToken(String token);

    void deleteAllByExpiresAtBefore(Date threshold);
}
