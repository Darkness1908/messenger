package com.relex.messenger.controller;

import com.relex.messenger.dto.AuthorizationForm;
import com.relex.messenger.dto.RegistrationForm;
import com.relex.messenger.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid RegistrationForm registrationForm) {
        authService.register(registrationForm);
        return ResponseEntity.ok("Verification letter has been sent");
    } //checked

    @PostMapping("/login")
    public ResponseEntity<?> logIn(
            @RequestBody @Valid AuthorizationForm authorizationForm) {
        String token = authService.logIn(authorizationForm);
        return ResponseEntity.ok(Map.of("token", token));
    } //checked
}
