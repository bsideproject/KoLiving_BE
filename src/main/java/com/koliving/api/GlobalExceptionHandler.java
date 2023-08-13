package com.koliving.api;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.dto.ConfirmationTokenErrorDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.ValidationResult;
import com.koliving.api.exception.ConfirmationTokenException;
import com.koliving.api.exception.DuplicateResourceException;
import com.koliving.api.i18n.MessageSource;
import com.koliving.api.user.SignUpStatus;
import com.koliving.api.user.User;
import com.koliving.api.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final UserService userService;

    @ExceptionHandler(value = {DuplicateResourceException.class, IllegalArgumentException.class})
    public ResponseEntity<ResponseDto<String>> handleRequestException(RuntimeException e, Locale locale) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        String errorMessage = getErrorMessage(e, locale);

        return createClientErrorResponse(errorMessage, badRequest);
    }

    @ExceptionHandler(value = ConfirmationTokenException.class)
    public ResponseEntity<ResponseDto<ConfirmationTokenErrorDto>> handleAuthException(ConfirmationTokenException e, HttpServletRequest request, Locale locale) {
        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);
        String email = e.getEmail();

        HttpStatus status = null;
        String redirectPath = null;

        switch (messageKey) {
            case "ungenerated_token", "expired_token" -> {
                status = HttpStatus.UNAUTHORIZED;
                redirectPath = "/login";
            }
            case "authenticated_token" -> {
                status = HttpStatus.FOUND;
                redirectPath = getRedirectLocation(email);
            }
        }

        return createRedirectResponse(new ConfirmationTokenErrorDto(errorMessage, email), status, getUri(request, redirectPath));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<ValidationResult>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationResult errors = ValidationResult.of(e);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return createClientErrorResponse(errors, badRequest);
    }

    @ExceptionHandler(value = AccessDeniedException.class) // Authorization
    public ResponseEntity<ResponseDto<String>> handleAccessDeniedException(RuntimeException e, HttpServletRequest request, Locale locale) {
        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        String redirectPath = "/login";

        return createRedirectResponse(errorMessage, httpStatus, getUri(request, redirectPath));
    }

    private URI getUri(HttpServletRequest req, String redirectPath) {
        URI currentUri = URI.create(req.getRequestURI());
        return UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toUri();
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

    @ExceptionHandler(value = KolivingServiceException.class)
    public ErrorResponse handleError(KolivingServiceException e) {
        log.error("handleError", e);
        return ErrorResponse.valueOf(e.getError());
    }

    private String getErrorMessage(RuntimeException e, Locale locale) {
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

    private <T> ResponseEntity<ResponseDto<T>> createRedirectResponse(T error, HttpStatus status, URI redirectUri) {
        ResponseDto<T> response = ResponseDto.failure(error, status.value());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);

        return new ResponseEntity<>(response, headers, status);
    }
}
