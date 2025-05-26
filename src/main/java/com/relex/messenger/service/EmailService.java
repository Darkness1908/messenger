package com.relex.messenger.service;

import com.relex.messenger.component.JwtService;
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

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final JwtService jwtService;

    public void sendSimpleEmail(User user, String subject, String body) {

        if (confirmationTokenRepository.existsByUser(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Token already exist");
        }

        String toEmail = user.getEmail();
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);
        confirmationTokenRepository.save(confirmationToken);
        String link = body + "http://localhost:5434/email/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2002phone2002@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
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
        confirmationTokenRepository.delete(confirmationToken);
        return jwtService.generateToken(user);
    }
}

