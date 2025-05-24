package com.relex.messenger.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordForm (
        @NotBlank String newPassword,
        @NotBlank String confirmPassword
) {

}
