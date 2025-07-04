package com.relex.messenger.service;

import com.relex.messenger.entity.JwtBlacklisted;
import com.relex.messenger.repository.JwtBlacklistedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtBlacklistService {

    private final JwtBlacklistedRepository jwtBlacklistedRepository;

    public void addTokenToBlacklist(String token, Date expired) {
        jwtBlacklistedRepository.save(new JwtBlacklisted(token, expired));
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtBlacklistedRepository.existsByToken(token);
    }

}
