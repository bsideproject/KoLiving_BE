package com.koliving.api.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Service
@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;
    private final EmailTemplateUtil emailTemplateUtil;
    private String mailHost;

    public EmailService(JavaMailSender mailSender, MessageSource messageSource, EmailTemplateUtil emailTemplateUtil, @Value("${spring.mail.username}") String mailHost) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
        this.emailTemplateUtil = emailTemplateUtil;
        this.mailHost = mailHost;
    }

    @Async
    @Override
    public void send(MailType type, String to, String link) {
        try {
            Locale currentLocale = LocaleContextHolder.getLocale();

            String title = "KOLIVING";
            String subtitle = messageSource.getMessage("auth_email_subtitle", null, currentLocale);
            String linkGuidance = messageSource.getMessage("auth_email_link_guidance", null, currentLocale);

            Map<String, Object> variables = new HashMap<>();
            variables.put("title", title);
            variables.put("subtitle", subtitle);
            variables.put("linkGuidance", linkGuidance);
            variables.put("authEmailLink", link);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String mailContent = emailTemplateUtil.generateEmail(type, variables);
            String subject = messageSource.getMessage("auth_email_subject", null, currentLocale);

            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(mailContent, true);
            helper.setFrom(mailHost);

            helper.addInline("logo", new ClassPathResource("static/image/logo-black.jpg"));

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to generate email", e);
            throw new MailParseException("failed to generate email", e);
        } catch (MailException e) {
            log.error("failed to send email", e);
            throw new MailSendException("Failed to send email", e);
        }
    }
}
