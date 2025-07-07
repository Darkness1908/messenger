package com.relex.messenger.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorizationForm (
    @NotBlank String login,
    @NotBlank String password
) {

}
