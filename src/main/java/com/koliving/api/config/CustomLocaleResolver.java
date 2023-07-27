package com.koliving.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

@Component
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    @Value("${server.default-locale:en}")
    private String defaultLocaleString;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale defaultLocale = Locale.forLanguageTag(defaultLocaleString);

        List<Locale> supportedLocales = Arrays.asList(Locale.ENGLISH, Locale.KOREAN);
        String headerLang = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

        if (headerLang == null) {
            return defaultLocale;
        }

        List<LanguageRange> headerLangList = LanguageRange.parse(headerLang);
        Locale currentLocale = Locale.lookup(headerLangList, supportedLocales);

        return currentLocale;
    }
}
