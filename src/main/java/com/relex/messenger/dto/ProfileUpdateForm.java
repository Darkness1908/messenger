package com.relex.messenger.dto;


import jakarta.validation.constraints.NotBlank;


public record ProfileUpdateForm(
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String patronymic
) {

}


