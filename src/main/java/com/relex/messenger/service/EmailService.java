package com.relex.messenger.service;

import com.relex.messenger.entity.ConfirmationToken;
import com.relex.messenger.entity.User;
import com.relex.messenger.repository.ConfirmationTokenRepository;
import com.relex.messenger.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public void sendSimpleEmail(String toEmail, User user) {
        if (user == null) {
            user = userRepository.findByEmail(toEmail).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "You must be registered")
            );

            if (confirmationTokenRepository.existsByUser(user)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "You already have an active confirmation token." +
                                " Use that one or wait until current token expires.");
            }

            if (user.isActivated()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You already have an activated account");
            }
        }

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);
        confirmationTokenRepository.save(confirmationToken);
        String link = "Перейдите по ссылке, чтобы активировать аккаунт: "
                + "http://localhost:5434/email/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2002phone2002@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Подтверждение регистрации");
        message.setText(link);

        mailSender.send(message);
    }

    @Transactional
    public String confirmVerificationToken(String token) {
        System.out.println(token);
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Token has been expired or never existed"));

        User user = confirmationTokenRepository.getUser(token);
        user.setActivated(true);
        user.setDeletedAt(null);
        confirmationTokenRepository.delete(confirmationToken);
        return jwtService.generateToken(user);
    }
}

