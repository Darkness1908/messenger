package com.relex.messenger.controller;

import com.relex.messenger.entity.User;
import com.relex.messenger.repository.ConfirmationTokenRepository;
import com.relex.messenger.repository.UserRepository;
import com.relex.messenger.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You must be registered")
        );

        if (confirmationTokenRepository.existsByUser(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You already have an active confirmation token." +
                            " Use that one or wait until current token expires.");
        }

        if (user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You already have an activated account");
        }

        emailService.sendSimpleEmail(
                user,
                "Подтверждение регистрации",
                "Перейдите по ссылке, чтобы активировать аккаунт: "
        );
        return ResponseEntity.ok("Letter has been sent");
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmVerificationToken(@RequestParam String token) {
        String JWT = emailService.confirmVerificationToken(token);
        return ResponseEntity.ok(Map.of("token", JWT));
    }
}
