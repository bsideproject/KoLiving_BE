package com.koliving.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpDto (
        @Email @NotBlank String email
) {
}
