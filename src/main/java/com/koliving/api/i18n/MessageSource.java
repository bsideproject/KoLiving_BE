package com.koliving.api.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageSource extends AbstractMessageSource {

    private final ResourceBundleMessageSource resourceBundleMessageSource;
    private final LanguageRepository languageRepository;

    @Override
    protected MessageFormat resolveCode(String key, Locale locale) {
        String messageContent;
        Language message = languageRepository.findByLocaleAndMessageKey(locale.toString(), key);

        if (message == null) {
            messageContent = resourceBundleMessageSource.getMessage(key, null, locale);
        } else {
            messageContent = message.getMessagePattern();
        }

        return new MessageFormat(messageContent, locale);
    }
}
