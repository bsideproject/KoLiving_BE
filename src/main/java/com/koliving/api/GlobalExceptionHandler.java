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
import com.koliving.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final UserService userService;
    private final HttpUtils httpUtils;

    final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
    final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;

    @ExceptionHandler(value = {DuplicateResourceException.class, IllegalArgumentException.class})
    public ResponseEntity<ResponseDto<String>> handleRequestException(RuntimeException e, Locale locale) {
        return httpUtils.createResponseEntity(
                httpUtils.createFailureResponse(getErrorMessage(e, locale), badRequest.value())
        );
    }

    @ExceptionHandler(value = ConfirmationTokenException.class)
    public ResponseEntity<ResponseDto<ConfirmationTokenErrorDto>> handleAuthException(ConfirmationTokenException e, HttpServletRequest request, Locale locale) {
        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        HttpStatus status = null;
        String redirectPath = null;
        String email = e.getEmail();

        switch (messageKey) {
            case "ungenerated_confirmation_token", "expired_confirmation_token" -> {
                status = HttpStatus.BAD_REQUEST;
                redirectPath = "/api/login";
            }
            case "authenticated_confirmation_token" -> {
                status = HttpStatus.UNAUTHORIZED;
                redirectPath = getSignUpUrl(request, email);
            }
        }

        return httpUtils.createResponseEntityWithRedirect(
                httpUtils.createFailureResponse(
                        new ConfirmationTokenErrorDto(errorMessage, email),
                        status.value()
                ),
                redirectPath
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<ValidationResult>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationResult errors = ValidationResult.of(e);

        return httpUtils.createResponseEntity(
                httpUtils.createFailureResponse(errors, badRequest.value())
        );
    }

    @ExceptionHandler(value = AccessDeniedException.class) // Authorization
    public ResponseEntity<ResponseDto<String>> handleAccessDeniedException(RuntimeException e, Locale locale) {
        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        return httpUtils.createResponseEntityWithRedirect(
                httpUtils.createFailureResponse(errorMessage, unauthorized.value()),
                "/api/login"
        );
    }

    @ExceptionHandler(value = KolivingServiceException.class)
    public ErrorResponse handleError(KolivingServiceException e) {
        log.error("handleError", e);
        return ErrorResponse.valueOf(e.getError());
    }

    private String getSignUpUrl(HttpServletRequest request, String email) {
        User user = (User) userService.loadUserByUsername(email);
        SignUpStatus currentSignUpStatus = user.getSignUpStatus();
        if (currentSignUpStatus.equals(SignUpStatus.PASSWORD_VERIFICATION_PENDING)) {
            return httpUtils.getRedirectUri(request, "/password");
        }

        if (currentSignUpStatus.equals(SignUpStatus.PROFILE_INFORMATION_PENDING)) {
            return httpUtils.getRedirectUri(request, "/profile");
        }

        if (currentSignUpStatus.equals(SignUpStatus.COMPLETED)) {
            return "/api/login";
        }

        return null;
    }

    private String getErrorMessage(RuntimeException e, Locale locale) {
        String[] messageKeyAndEmail = e.getMessage().split(":");
        String messageKey = messageKeyAndEmail[0];
        String email = messageKeyAndEmail[1];

        String errorMessage = messageSource.getMessage(messageKey, new Object[]{email}, locale);

        return errorMessage;
    }
}
