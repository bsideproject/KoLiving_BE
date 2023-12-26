package com.koliving.api.email;

import com.koliving.api.properties.EmailProperties;
import com.koliving.api.properties.FrontProperties;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.infra.RoomContactEvent;
import com.koliving.api.user.domain.User;
import com.koliving.api.utils.HttpUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;
    private final EmailTemplateUtil emailTemplateUtil;
    private final EmailProperties emailProperties;
    private final HttpUtils httpUtils;

    @Async("mailExecutor")
    @Override
    public void sendRoomContact(String to, String contact, String message, User sender, String link) {
        try {
            Locale currentLocale = httpUtils.getLocaleForLanguage(LocaleContextHolder.getLocale());

            Map<String, Object> variables = new HashMap<>();
            variables.put("title", "KOLIVING");
            variables.put("subtitle", messageSource.getMessage("contact_email_subtitle", null, currentLocale));
            variables.put("contact", contact);
            variables.put("message", message);
            variables.put("roomLink", link);
            variables.put("userName", sender.getFullName());
            variables.put("userAge", sender.getAge());
            variables.put("userImageProfile", sender.getImageProfile());
            variables.put("userGender", sender.getGender());
            variables.put("userDescription", sender.getDescription());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setSubject(messageSource.getMessage("contact_email_subject", null, currentLocale));
            helper.setTo(to);
            helper.setText(emailTemplateUtil.generateEmail(MailType.CONTACT, variables), true);
            helper.setFrom(emailProperties.getUsername());
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

    @Async("mailExecutor")
    @Override
    public void sendMailAuth(MailType type, String to, String link) {
        try {
            Locale currentLocale = httpUtils.getLocaleForLanguage(LocaleContextHolder.getLocale());

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
            helper.setFrom(emailProperties.getUsername());
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

    @Async("mailExecutor")
    @Override
    public void sendRoomReport(String roomLink, String reportReason, User reporter) {
        try {
            Locale currentLocale = httpUtils.getLocaleForLanguage(LocaleContextHolder.getLocale());

            Map<String, Object> variables = new HashMap<>();
            variables.put("title", "KOLIVING");
            variables.put("roomLink", roomLink);
            variables.put("userName", reporter.getFullName());
            variables.put("userAge", reporter.getAge());
            variables.put("userImageProfile", reporter.getImageProfile());
            variables.put("userGender", reporter.getGender());
            variables.put("userDescription", reporter.getDescription());
            variables.put("reason", reportReason);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setSubject(messageSource.getMessage("report_email_subject", null, currentLocale));
            helper.setTo(emailProperties.getUsername());
            helper.setText(emailTemplateUtil.generateEmail(MailType.REPORT, variables), true);
            helper.setFrom(emailProperties.getUsername());
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
