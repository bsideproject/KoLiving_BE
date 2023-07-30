package com.koliving.api.dto;

import com.koliving.api.annotation.PasswordConstraint;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public record SetPasswordDto (
        @NotBlank
        @Range(min = 8L, max = 20L)
        @PasswordConstraint
        String password
        ) {
}
