package com.koliving.api.token;

import com.koliving.api.clock.IClock;
import com.koliving.api.email.IEmailService;
import com.koliving.api.email.MailType;
import com.koliving.api.properties.EmailProperties;
import com.koliving.api.token.confirmation.ConfirmationToken;
import com.koliving.api.token.confirmation.ConfirmationTokenRepository;
import com.koliving.api.token.confirmation.ConfirmationTokenService;
import com.koliving.api.utils.HttpUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    IEmailService emailSender;

    @Mock
    IClock clock;

    @Mock
    HttpUtils httpUtils;

    @Mock
    EmailProperties emailProperties;

    @InjectMocks
    ConfirmationTokenService confirmationTokenService;

    @Test
    @DisplayName("get() : token 값에 따른 행이 존재함")
    void get_success() {
        String testMail = "from@test.com";

        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .build();
        String tokenValue = token.getToken();

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        Optional<ConfirmationToken> result = confirmationTokenService.get(tokenValue);

        verify(confirmationTokenRepository, times(1)).findByToken(anyString());

        assertTrue(result.isPresent());
        assertEquals(token, result.get());
        assertEquals(tokenValue, result.get().getToken());
    }

    @Test
    @DisplayName("get() : token 값에 따른 행이 없음")
    void get_failure() {

        // 생성자 함수를 통해 UUID.randomUUID().toString() 생성값이 token 값으로 할당됨
        // 문자열로만 구성된 token 값은 존재하지 않음 : 실패 테스트 케이스의 token 값으로 사용
        String nonExistentTokenValue = "invalid token value";

        when(confirmationTokenRepository.findByToken(nonExistentTokenValue)).thenReturn(Optional.empty());

        Optional<ConfirmationToken> result = confirmationTokenService.get(nonExistentTokenValue);

        verify(confirmationTokenRepository, times(1)).findByToken(anyString());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("create() : 올바른 email 형식")
    void create_success() {
        String testMail = "new@test.com";
        when(emailProperties.getAuthValidityPeriod()).thenReturn(30L);
        ConfirmationToken result = confirmationTokenService.create(testMail);

        verify(emailProperties, times(1)).getAuthValidityPeriod();

        assertNotNull(result);

        // UUID 형식의 토큰값이 생성되었는지 확인
        assertDoesNotThrow(() -> {
            String tokenValue = result.getToken();
            UUID.fromString(tokenValue);
        });
        assertEquals(result.getEmail(), testMail);

        // expiredAt 시간이 현재 시간으로부터 30분 이후의 시간값이 생성되었는지 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resultExpiresAt = result.getExpiredDate();
        assertTrue(resultExpiresAt.isAfter(now) && resultExpiresAt.isBefore(now.plusMinutes(emailProperties.getAuthValidityPeriod())));
        assertEquals(result.isBResended(), false);
        assertEquals(result.isBConfirmed(), false);
    }

    // createToken_failure() 생략
    // 컨트롤러 단에서 Java Bean Validation 기술을 사용한 유효성 검사를 수행하므로 검증 로직이 없음

    @Test
    @DisplayName("save() : crudRepository save api 정상 호출")
    void save_success() {
        String testMail = "new@test.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail).build();

        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenReturn(token);

        ConfirmationToken result = confirmationTokenService.save(token);

        verify(confirmationTokenRepository, times(1)).save(token);

        assertNotNull(result);
        assertEquals(result, token);
    }

    @Test
    @DisplayName("sendEmail_success() : emailSender에서 sendEmail 메서드 한번 호출 확인")
    void sendEmail_success() {
        String recipientEmail = "test@example.com";
        String tokenValue = UUID.randomUUID().toString();
        String authLinkPath = "http://localhost:8080" + "/api/v1/sign-up/confirm";
        String authLinkQueryString = "?token=" + tokenValue + "&email=" + recipientEmail;
        String authLinkUrl = authLinkPath + authLinkQueryString;

        when(httpUtils.getCurrentVersionUrl(anyString())).thenReturn(authLinkPath);

        confirmationTokenService.sendEmail(recipientEmail, tokenValue);

        verify(emailSender, times(1)).send(MailType.AUTH, recipientEmail, authLinkUrl);
    }

    @Test
    @DisplayName("authenticateToken_success() : ConfirmationToken 인증 여부 확인")
    void authenticateToken_success() {
        when(emailProperties.getAuthValidityPeriod()).thenReturn(30L);

        String testMail = "test@example.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(emailProperties.getAuthValidityPeriod())
                .build();

        when(confirmationTokenService.get(anyString())).thenReturn(Optional.of(token));
        when(clock.now()).thenReturn(LocalDateTime.now());

        String tokenValue = token.getToken();
        confirmationTokenService.authenticateToken(tokenValue);

        assertTrue(confirmationTokenService.get(anyString()).isPresent());
        assertEquals(token.isBConfirmed(), true);
    }

    @Test
    @DisplayName("authenticateToken_failure() : ConfirmationToken 만료됨")
    void authenticateToken_failure_expired_confirmation_token() {
        when(emailProperties.getAuthValidityPeriod()).thenReturn(30L);

        String testMail = "test@example.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(emailProperties.getAuthValidityPeriod())
                .build();
        String tokenValue = token.getToken();

        Random rand = new Random();
        long min = emailProperties.getAuthValidityPeriod() + 1;  // 범위를 넘어가는 최소값
        long max = 1440L;               // 24시간. expiredAt 값의 범위를 검증하는데 충분한 최대값
        long expirationMinutes = min + ((long)(rand.nextDouble()*(max - min)));

        when(confirmationTokenService.get(anyString())).thenReturn(Optional.of(token));

        // clock.now() 의 반환값이 유효기간 만료여부의 기준이 됨
        when(clock.now()).thenReturn(LocalDateTime.now().plusMinutes(expirationMinutes));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            confirmationTokenService.authenticateToken(tokenValue);
        });

        assertTrue(confirmationTokenService.get(anyString()).isPresent());
        assertEquals(e.getMessage(), "expired_confirmation_token");
        assertFalse(token.isBConfirmed());
    }

    @Test
    @DisplayName("authenticateToken_failure() : 이미 인증된 ConfirmationToken")
    void authenticateToken_failure_authenticated_confirmation_token() {
        when(emailProperties.getAuthValidityPeriod()).thenReturn(30L);

        String testMail = "test@example.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(emailProperties.getAuthValidityPeriod())
                .build();

        when(confirmationTokenService.get(anyString())).thenAnswer(invocation -> {
            token.confirm();            // already confirmed
            return Optional.of(token);  // not empty
        });
        when(clock.now()).thenReturn(LocalDateTime.now());  // not expired

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            confirmationTokenService.authenticateToken(token.getToken());
        });

        assertTrue(confirmationTokenService.get(anyString()).isPresent());
        assertEquals(e.getMessage(), "authenticated_confirmation_token");
        assertTrue(token.isBConfirmed());
    }

    @Test
    @DisplayName("authenticateToken_failure() : 서버로부터 생성되지 않은 ConfirmationToken")
    void authenticateToken_failure_not_generated_by_server() {
        String nonExistentTokenValue = "invalid token value";

        when(confirmationTokenService.get(anyString())).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            confirmationTokenService.authenticateToken(nonExistentTokenValue);
        });

        assertEquals(e.getMessage(), "ungenerated_confirmation_token");
    }
}
