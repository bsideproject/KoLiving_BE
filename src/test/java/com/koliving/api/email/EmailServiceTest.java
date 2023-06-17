package com.koliving.api.email;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

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

    @InjectMocks
    private EmailService emailService;

    private String mailHost = "from@test.com";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(emailService, "mailHost", mailHost);

        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);
        MimeMessage mimeMessage = new MimeMessage(session);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("test");
        when(emailTemplateUtil.generateEmail(any(), any())).thenReturn("test");
    }

    @Test
    @DisplayName("sendEmail : SMTP 서버 요청 성공여부 확인")
    public void sendEmail_success() throws MailException {
        emailService.send(MailType.AUTH, "to@test.com", "testLink");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
