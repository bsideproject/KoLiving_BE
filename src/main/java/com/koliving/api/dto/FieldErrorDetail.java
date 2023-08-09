package com.koliving.api.dto;

import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;

import java.util.Locale;

public record FieldErrorDetail(String objectName, String field, String code, String message) {

    public static FieldErrorDetail of(FieldError error) {
        return new FieldErrorDetail(
                error.getObjectName(),
                error.getField(),
                error.getCode(),
                error.getDefaultMessage()
        );
    }

    public static FieldErrorDetail of(FieldError error, MessageSource messageSource, Locale locale) {
        return new FieldErrorDetail(
                error.getObjectName(),
                error.getField(),
                error.getCode(),
                messageSource.getMessage(convertCodeToMessageTemplate(error.getCode()), null, locale)
        );
    }

    private static String convertCodeToMessageTemplate(String errorCode) {
        return String.format("jakarta.validation.constraints.%s.message", errorCode);
    }
}
