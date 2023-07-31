package com.koliving.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ProfileDto(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotNull
        @Range(min = 0L, max=2L)
        int genderCode,

        @NotNull
        @Past
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @NotBlank
        @Size(min = 0, max =500)
        String description) {
}
