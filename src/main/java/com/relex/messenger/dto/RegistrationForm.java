package com.relex.messenger.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistrationForm (
    @NotBlank String name,
    @NotBlank String surname,
    @NotBlank String patronymic,
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String email,
    @NotBlank String phoneNumber
) {

}

