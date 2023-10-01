package com.koliving.api;

import static com.koliving.api.token.confirmation.ConfirmationTokenType.RESET_PASSWORD;
import static com.koliving.api.token.confirmation.ConfirmationTokenType.SIGN_UP;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.dto.ConfirmationTokenErrorDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.ValidationResult;
import com.koliving.api.exception.ConfirmationTokenException;
import com.koliving.api.exception.DuplicateResourceException;
import com.koliving.api.exception.NonExistentResourceException;
import com.koliving.api.exception.PasswordInvalidException;
import com.koliving.api.i18n.MessageSource;
import com.koliving.api.token.confirmation.ConfirmationToken;
import com.koliving.api.token.confirmation.ConfirmationTokenService;
import com.koliving.api.token.confirmation.ConfirmationTokenType;
import com.koliving.api.user.SignUpStatus;
import com.koliving.api.user.User;
import com.koliving.api.user.application.UserService;
import com.koliving.api.utils.HttpUtils;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final HttpUtils httpUtils;

    final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
    final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;

    @ExceptionHandler(value = {DuplicateResourceException.class, NonExistentResourceException.class})
    public ResponseEntity<ResponseDto<String>> handleRequestException(RuntimeException e, Locale locale) {
        locale = httpUtils.getLocaleForLanguage(locale);

        return httpUtils.createResponseEntity(
            httpUtils.createFailureResponse(getErrorMessage(e, locale), badRequest.value())
        );
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<String>> handleIllegalArgumentException(IllegalArgumentException e,
        Locale locale) {
        locale = httpUtils.getLocaleForLanguage(locale);

        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        return httpUtils.createResponseEntity(
            httpUtils.createFailureResponse(errorMessage, badRequest.value())
        );
    }

    @ExceptionHandler(value = PasswordInvalidException.class)
    public ResponseEntity<ResponseDto<String>> handlePasswordInvalidException(PasswordInvalidException e,
        Locale locale) {
        locale = httpUtils.getLocaleForLanguage(locale);

        return httpUtils.createResponseEntity(
            httpUtils.createFailureResponse(getErrorMessage(e, locale), badRequest.value())
        );
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleUsernameNotFoundException(UsernameNotFoundException e,
        Locale locale) {
        locale = httpUtils.getLocaleForLanguage(locale);
        String email = extractEmail(e);

        String errorMessage = messageSource.getMessage("email_not_exists", new Object[]{email}, locale);

        return httpUtils.createResponseEntity(
            httpUtils.createFailureResponse(errorMessage, badRequest.value())
        );
    }

    @ExceptionHandler(value = ConfirmationTokenException.class)
    public ResponseEntity<ResponseDto<ConfirmationTokenErrorDto>> handleAuthException(ConfirmationTokenException e,
        Locale locale) {
        locale = httpUtils.getLocaleForLanguage(locale);

        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        HttpStatus status = null;
        String redirectPath = null;
        String email = e.getEmail();

        switch (messageKey) {
            case "ungenerated_confirmation_token", "expired_confirmation_token" -> {
                status = HttpStatus.BAD_REQUEST;
                redirectPath = getRedirectUrl("/login");
            }
            case "authenticated_confirmation_token" -> {
                status = HttpStatus.UNAUTHORIZED;
                ConfirmationToken confirmationToken = confirmationTokenService.get(e.getToken()).get();
                redirectPath = getConfirmationTokenRedirectUrl(confirmationToken.getTokenType(), email);
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
    public ResponseEntity<ResponseDto<ValidationResult>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        ValidationResult errors = ValidationResult.of(e);

        return httpUtils.createResponseEntity(
            httpUtils.createFailureResponse(errors, badRequest.value())
        );
    }

    @ExceptionHandler(value = AccessDeniedException.class) // Authorization
    public ResponseEntity<ResponseDto<String>> handleAccessDeniedException(RuntimeException e, Locale locale) {
        locale = httpUtils.getLocaleForLanguage(locale);

        String messageKey = e.getMessage();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        return httpUtils.createResponseEntityWithRedirect(
            httpUtils.createFailureResponse(errorMessage, unauthorized.value()),
            httpUtils.getCurrentVersionPath("login")
        );
    }

    @ExceptionHandler(value = KolivingServiceException.class)
    public ResponseEntity<ErrorResponse> handleError(KolivingServiceException e) {
        log.error("handleError", e);
        return ResponseEntity.status(e.getStatus())
            .body(ErrorResponse.valueOf(e.getError()));
    }

    private String extractEmail(UsernameNotFoundException e) {
        String message = e.getMessage();

        return message.substring(message.lastIndexOf("email"), message.indexOf("not")).trim();
    }

    private String getRedirectUrl(String path) {
        return httpUtils.getFrontUrl(path);
    }

    private String getConfirmationTokenRedirectUrl(ConfirmationTokenType tokenType, String email) {
        if (tokenType == SIGN_UP) {
            return getSignUpRedirectUrl(email);
        } else if (tokenType == RESET_PASSWORD) {
            return getRedirectUrl(tokenType.getRedirectPath());
        }

        return null;
    }

    private String getSignUpRedirectUrl(String email) {
        User user = (User) userService.loadUserByUsername(email);
        SignUpStatus currentSignUpStatus = user.getSignUpStatus();

        return getRedirectUrl(currentSignUpStatus.getRedirectUrl());
    }

    private String getErrorMessage(RuntimeException e, Locale locale) {
        String[] messageKeyAndEmail = e.getMessage().split(":");
        String messageKey = messageKeyAndEmail[0];
        String email = messageKeyAndEmail[1];

        String errorMessage = messageSource.getMessage(messageKey, new Object[]{email}, locale);

        return errorMessage;
    }
}
