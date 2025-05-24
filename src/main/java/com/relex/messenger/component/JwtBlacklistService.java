package com.relex.messenger.component;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class JwtBlacklistService {

    private final Set<String> blacklistedTokens = new HashSet<>();

    public void addTokenToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
