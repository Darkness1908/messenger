package com.relex.messenger.controller;

import com.relex.messenger.dto.AuthorizationForm;
import com.relex.messenger.dto.RegistrationForm;
import com.relex.messenger.service.AuthService;
import com.relex.messenger.service.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
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
        return ResponseEntity.ok(Map.of("access token", JWT[0], "refresh token", JWT[1]));
    } //checked

    @PostMapping("/refresh-tokens")
    public ResponseEntity<?> refreshTokens(@RequestBody String refreshToken) {
        String[] JWT = jwtService.refreshTokens(refreshToken);
        return ResponseEntity.ok(Map.of("access token", JWT[0], "refresh token", JWT[1]));
    }
}
