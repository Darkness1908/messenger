package com.relex.messenger.controller;

import com.relex.messenger.repository.ConfirmationTokenRepository;
import com.relex.messenger.repository.UserRepository;
import com.relex.messenger.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam String email) {
        emailService.sendSimpleEmail(
                email,
                null
        );
        return ResponseEntity.ok("Letter has sent");
    }

    @GetMapping("/")
    public ResponseEntity<?> confirmVerificationToken(@RequestParam String token) {
        String JWT = emailService.confirmVerificationToken(token);
        return ResponseEntity.ok(Map.of("token", JWT));
    }
}
