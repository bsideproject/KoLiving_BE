package com.koliving.api.email;

import com.koliving.api.properties.EmailProperties;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MessageSource messageSource;

    @Mock
    private EmailTemplateUtil emailTemplateUtil;

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private EmailService emailService;

    @Disabled
    @Test
    @DisplayName("sendEmail : SMTP 서버 요청 성공여부 확인")
    public void sendEmail_success() throws MailException {
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);
        MimeMessage mimeMessage = new MimeMessage(session);

        when(emailProperties.getUsername()).thenReturn("from@test.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("test");
        when(emailTemplateUtil.generateEmail(any(), any())).thenReturn("test");

        emailService.sendMailAuth(MailType.AUTH, "to@test.com", "testLink");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
