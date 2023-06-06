package com.koliving.api.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component("messageSource")
public class DBMessageSource extends AbstractMessageSource {

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    protected MessageFormat resolveCode(String key, Locale locale) {
        Language message = languageRepository.findByLocaleAndMessageKey(locale.toString(), key);

        return new MessageFormat(message.getMessageContent(), locale);
    }
}

