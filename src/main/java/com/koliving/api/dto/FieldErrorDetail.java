package com.koliving.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@AllArgsConstructor
public class FieldErrorDetail {

    private final String objectName;
    private final String field;
    private final String code;
    private final String message;

    public static FieldErrorDetail of(FieldError error) {
        return new FieldErrorDetail(
                error.getObjectName(),
                error.getField(),
                error.getCode(),
                error.getDefaultMessage()
        );
    }
}
