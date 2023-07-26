package com.koliving.api.exception;

import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationResult errors = ValidationResult.of(e);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        int value = badRequest.value();

        return new ResponseEntity<>(ResponseDto.failure(errors ,value), badRequest);
    }
}
