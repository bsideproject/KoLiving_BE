package com.koliving.api.dto;

import org.springframework.validation.Errors;

import java.util.List;
import java.util.stream.Collectors;

public record ValidationResult(List<FieldErrorDetail> errors) {

    public static ValidationResult of(Errors errors) {
        List<FieldErrorDetail> details =
                errors.getFieldErrors()
                        .stream()
                        .map(error -> FieldErrorDetail.of(error))
                        .collect(Collectors.toList());
        return new ValidationResult(details);
    }
}
