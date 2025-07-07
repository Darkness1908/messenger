package com.relex.messenger.controller;

import com.relex.messenger.service.EmailService;
import com.relex.messenger.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;
    private final SseService sseService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam String email) {
        emailService.sendSimpleEmail(
                email,
                null
        );
        return ResponseEntity.ok("Letter has sent");
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmVerificationToken(@RequestParam String token) throws IOException {
        String[] JWT = emailService.confirmVerificationToken(token);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", JWT[1])
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();

        sseService.pushToken(JWT[0]);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("Account has been confirmed");
    }
}
