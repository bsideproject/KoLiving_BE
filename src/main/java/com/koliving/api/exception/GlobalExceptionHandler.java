package com.koliving.api.exception;

import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.ValidationResult;
import com.koliving.api.i18n.MessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<ValidationResult>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationResult errors = ValidationResult.of(e);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return createClientErrorResponse(errors, badRequest);
    }

    @ExceptionHandler(value = {DuplicateResourceException.class, IllegalArgumentException.class})
    public ResponseEntity<ResponseDto<String>> handleRequestException(DuplicateResourceException e, Locale locale) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        String errorMessage = getErrorMessage(e, locale);

        return createClientErrorResponse(errorMessage, badRequest);
    }

    private String getErrorMessage(DuplicateResourceException e, Locale locale) {
        String[] messageKeyAndEmail = e.getMessage().split(":");
        String messageKey = messageKeyAndEmail[0];
        String email = messageKeyAndEmail[1];

        String errorMessage = messageSource.getMessage(messageKey, new Object[]{email}, locale);

        return errorMessage;
    }

    private <T> ResponseEntity<ResponseDto<T>> createClientErrorResponse(T error, HttpStatus status) {
        ResponseDto<T> response = ResponseDto.failure(error, status.value());

        return new ResponseEntity<>(response, status);
    }
}
