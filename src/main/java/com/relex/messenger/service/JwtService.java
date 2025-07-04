package com.relex.messenger.service;

import com.relex.messenger.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final UserRepository userRepository;
    private final JwtBlacklistService jwtBlacklistService;
    private final Key accessKey;
    private final Key refreshKey;

    public JwtService(@Value("${accessJwt.secret}") String accessSecret,
                      @Value("${refreshJwt.secret}") String refreshSecret,
                      JwtBlacklistService jwtBlacklistService,
                      UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtBlacklistService = jwtBlacklistService;
        byte[] decodedKey = Base64.getDecoder().decode(accessSecret);
        this.accessKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        decodedKey = Base64.getDecoder().decode(refreshSecret);
        this.refreshKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String[] generateTokens(Long userId, String username) {
        return new String[] {
                Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .signWith(accessKey)
                .compact(),

                Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(refreshKey)
                .compact()
        };
    }

    public String[] refreshTokens(String token) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid token format");
        }

        if (jwtBlacklistService.isTokenBlacklisted(token)) {
            throw new JwtException("Token is blacklisted");
        }

        Long id = Long.parseLong(claims.getSubject());
        String username = userRepository.getUsernameById(Long.parseLong(id.toString()));

        blacklistToken(token);

        return generateTokens(id, username);
    }

    public void blacklistToken(String token) {
        jwtBlacklistService.addTokenToBlacklist(token, getExpiration(token));
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(accessKey).build()
                    .parseClaimsJws(token).getBody();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public Date getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(refreshKey).build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

}