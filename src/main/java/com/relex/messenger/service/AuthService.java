package com.relex.messenger.service;

import com.relex.messenger.dto.AuthorizationForm;
import com.relex.messenger.dto.RegistrationForm;
import com.relex.messenger.entity.User;
import com.relex.messenger.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Transactional
    public void register(@NotNull RegistrationForm registrationForm) {
        if (!isValidEmail(registrationForm.email())) {
            throw new IllegalArgumentException("Incorrect email");
        }

        if (!isValidPhoneNumber(registrationForm.phoneNumber())) {
            throw new IllegalArgumentException("Incorrect number");
        }

        if (userRepository.existsByEmail((registrationForm.email()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email is already taken");
        }

        if (userRepository.existsByPhoneNumber((registrationForm.phoneNumber()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Phone number is already taken");
        }

        if (userRepository.existsByUsername((registrationForm.username()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username is already taken");
        }

        String password = passwordEncoder.encode(registrationForm.password());
        User user = new User(registrationForm, password);
        userRepository.save(user);
        emailService.sendSimpleEmail(
                user.getEmail(),
                user
        );
    }

    public String logIn(@NotNull AuthorizationForm authorizationForm) {
        if (incorrectLogin(authorizationForm.login())) {
            throw new IllegalArgumentException("Incorrect email or phone number");
        }

        User user;
        if (isValidEmail(authorizationForm.login())) {
            user = userRepository.findByEmail(authorizationForm.login()).
                    orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "User not found"));
        }
        else {
            user = userRepository.findByPhoneNumber(authorizationForm.login()).
                    orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "User not found"));
        }

        if (!passwordEncoder.matches(authorizationForm.password(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        if (!user.isActivated()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is not activated");
        }

        user.setDeletedAt(null);
        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    @Contract(pure = true)
    private boolean isValidEmail(@NotNull String login) {
        return login.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }

    @Contract(pure = true)
    private boolean isValidPhoneNumber(@NotNull String login) {
        return login.matches("^\\+?[0-9]{1,4}?[-.\\s]?[0-9]{1,3}[-.\\s]?[0-9]{3}[-.\\s]?[0-9]{4}$");
    }

    private boolean incorrectLogin(String login) {
        return !(isValidEmail(login) || isValidPhoneNumber(login));
    }
}
