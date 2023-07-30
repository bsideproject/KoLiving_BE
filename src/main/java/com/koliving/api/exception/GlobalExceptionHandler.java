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
    public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationResult errors = ValidationResult.of(e);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        int value = badRequest.value();

        return new ResponseEntity<>(ResponseDto.failure(errors ,value), badRequest);
    }

    @ExceptionHandler(value = {DuplicateResourceException.class, IllegalArgumentException.class})
    public ResponseEntity<ResponseDto> handleRequestException(DuplicateResourceException e, Locale locale) {
        String[] messageKeyAndEmail = e.getMessage().split(":");
        String messageKey = messageKeyAndEmail[0];
        String email = messageKeyAndEmail[1];
        String errorMessage = messageSource.getMessage(messageKey, new Object[]{email}, locale);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        int value = badRequest.value();

        return new ResponseEntity<>(ResponseDto.failure(errorMessage ,value), badRequest);
    }
}
