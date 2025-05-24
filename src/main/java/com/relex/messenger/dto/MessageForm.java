package com.relex.messenger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageForm (
    @NotBlank String message,
    @NotNull Long chatId
) {

}
