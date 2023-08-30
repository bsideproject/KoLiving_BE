package com.koliving.api.config;

import com.koliving.api.properties.ServerProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

@Component
@RequiredArgsConstructor
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    private final ServerProperties serverProperties;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        List<Locale> supportedLocales = Arrays.asList(Locale.ENGLISH, Locale.KOREAN);
        String headerLang = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

        if (headerLang == null) {
            return Locale.forLanguageTag(serverProperties.getDefaultLocale());
        }

        return Locale.lookup(LanguageRange.parse(headerLang), supportedLocales);
    }
}
