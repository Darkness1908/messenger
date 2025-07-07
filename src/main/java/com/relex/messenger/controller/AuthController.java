package com.relex.messenger.controller;

import com.relex.messenger.dto.AuthorizationForm;
import com.relex.messenger.dto.RegistrationForm;
import com.relex.messenger.service.AuthService;
import com.relex.messenger.service.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid RegistrationForm registrationForm) {
        authService.register(registrationForm);
        return ResponseEntity.ok("Verification letter has been sent");
    } //checked

    @PostMapping("/login")
    public ResponseEntity<?> logIn(
            @RequestBody @Valid AuthorizationForm authorizationForm) {
        String[] JWT = authService.logIn(authorizationForm);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", JWT[1])
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(30)) // например, 7 дней
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("access token", JWT[0]));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(
            @CookieValue(name = "refreshToken", required = false) String token) {
        if (token != null && !token.isEmpty()) {
            jwtService.blacklistToken(token);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) //
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logged out");
    } //checked

    @PostMapping("/refresh-tokens")
    public ResponseEntity<?> refreshTokens(@RequestBody String refreshToken) {
        String[] JWT = jwtService.refreshTokens(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", JWT[1])
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(30)) // например, 7 дней
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("access token", JWT[0]));    }
}
