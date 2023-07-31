package com.koliving.api.dto;

import org.springframework.validation.FieldError;

public record FieldErrorDetail(String objectName, String field, String code, String message) {

    public static FieldErrorDetail of(FieldError error) {
        return new FieldErrorDetail(
                error.getObjectName(),
                error.getField(),
                error.getCode(),
                error.getDefaultMessage()
        );
    }
}
