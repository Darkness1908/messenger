package com.relex.messenger.service;

import com.relex.messenger.entity.JwtBlacklisted;
import com.relex.messenger.repository.JwtBlacklistedRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtBlacklistService {

    private final JwtBlacklistedRepository jwtBlacklistedRepository;
    private final Key key;

    public JwtBlacklistService(JwtBlacklistedRepository jwtBlacklistedRepository,
                               @Value("${jwt.secret}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        this.jwtBlacklistedRepository = jwtBlacklistedRepository;
    }

    public void addTokenToBlacklist(String token) {
        jwtBlacklistedRepository.save(new JwtBlacklisted(token, getExpiration(token)));
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtBlacklistedRepository.existsByToken(token);
    }

    private Date getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }
}
