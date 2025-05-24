package com.relex.messenger.controller;


import com.relex.messenger.repository.UserRepository;
import com.relex.messenger.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationToken(@RequestParam String email) {
        emailService.sendSimpleEmail(
                email, // Кому отправить
                "Подтверждение регистрации",
                "Перейдите по ссылке, чтобы активировать аккаунт: "
        );
        return ResponseEntity.ok("Verification letter has been sent");
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmVerificationToken(@RequestParam String token) {
        String JWT = emailService.confirmVerificationToken(token);
        return ResponseEntity.ok(Map.of("token", JWT));
    }
}
