package com.koliving.api.exception;

import com.koliving.api.dto.AuthErrorDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.ValidationResult;
import com.koliving.api.i18n.MessageSource;
import com.koliving.api.user.SignUpStatus;
import com.koliving.api.user.User;
import com.koliving.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final UserService userService;

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

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<ResponseDto<AuthErrorDto>> handleAuthException(AuthException e, ServerHttpRequest req, Locale locale) {
        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        String email = e.getEmail();

        HttpStatus status = null;
        String redirectPath = null;

        switch (e.getMessage()) {
            case "ungenerated_token", "expired_token" -> {
                status = HttpStatus.UNAUTHORIZED;
                redirectPath = "/login";
            }
            case "authenticated_token" -> {
                status = HttpStatus.FOUND;
                redirectPath = getRedirectLocation(email);
            }
        }

        return createRedirectResponse(new AuthErrorDto(errorMessage, email), req, redirectPath, status);
    }

    private String getRedirectLocation(String email) {
        User user = (User) userService.loadUserByUsername(email);
        if (user.getSignUpStatus().equals(SignUpStatus.PROFILE_INFORMATION_PENDING)) {
            return "/profile";
        } else if (user.getSignUpStatus().equals(SignUpStatus.COMPLETED)) {
            return "/login";
        }
        
        return null;
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

    private <T> ResponseEntity<ResponseDto<T>> createRedirectResponse(T error, ServerHttpRequest req, String redirectPath, HttpStatus status) {
        ResponseDto<T> response = ResponseDto.failure(error, status.value());

        URI currentUri = req.getURI();
        URI redirectUri = UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);

        return new ResponseEntity<>(response, headers, status);
    }
}
