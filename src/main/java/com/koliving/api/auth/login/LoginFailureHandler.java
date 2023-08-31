package com.koliving.api.auth.login;

import com.koliving.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final HttpUtils httpUtils;
    private final MessageSource messageSource;
    private final LocaleResolver customLocaleResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        Locale locale = customLocaleResolver.resolveLocale(request);
        String failureMessage = messageSource.getMessage("login_exception", null, locale);

        httpUtils.setResponseWithRedirect(
                response,
                httpUtils.createFailureResponse(failureMessage, HttpStatus.UNAUTHORIZED.value()),
                httpUtils.getCurrentVersionPath("login")
        );
    }
}
