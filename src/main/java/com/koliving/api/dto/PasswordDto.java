package com.koliving.api.dto;

import com.koliving.api.annotation.PasswordConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordDto(
        @NotBlank
        @Size(min = 8, max = 20)
        @PasswordConstraint
        String password
        ) {
}
