package com.relex.messenger.dto;

import jakarta.validation.constraints.NotBlank;

public record GroupForm (
    @NotBlank String groupName,
    String description
) {

}
