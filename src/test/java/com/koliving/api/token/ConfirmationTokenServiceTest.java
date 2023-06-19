package com.koliving.api.token;

import com.koliving.api.clock.IClock;
import com.koliving.api.email.IEmailService;
import com.koliving.api.email.MailType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    ConfirmationTokenService confirmationTokenService;

    String origin = "http://localhost:8080";
    String currentVersion = "v1";
    long validityPeriod = 30;

    @BeforeEach
    public void setUp() {
        confirmationTokenService = new ConfirmationTokenService(
                confirmationTokenRepository,
                emailSender,
                clock,
                origin,
                currentVersion,
                validityPeriod
        );
    }

    @Test
    @DisplayName("getToken() : token 값에 따른 행이 존재함")
    void getToken_success() {
        String testMail = "from@test.com";

        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .build();
        String tokenValue = token.getToken();

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(tokenValue);

        assertTrue(result.isPresent());
        assertEquals(token, result.get());
        assertEquals(tokenValue, result.get().getToken());

        verify(confirmationTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("getToken() : token 값에 따른 행이 없음")
    void getToken_failure() {

        // 생성자 함수를 통해 UUID.randomUUID().toString() 생성값이 token 값으로 할당됨
        // 문자열로만 구성된 token 값은 존재하지 않음 : 실패 테스트 케이스의 token 값으로 사용
        String nonExistentTokenValue = "invalid token value";

        when(confirmationTokenRepository.findByToken(nonExistentTokenValue)).thenReturn(Optional.empty());

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(nonExistentTokenValue);

        assertTrue(result.isEmpty());

        verify(confirmationTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("createToken() : 올바른 email 형식")
    void createToken_success() {
        String testMail = "new@test.com";
        ConfirmationToken result = confirmationTokenService.createToken(testMail);

        assertNotNull(result);

        // UUID 형식의 토큰값이 생성되었는지 확인
        assertDoesNotThrow(() -> {
            String tokenValue = result.getToken();
            UUID.fromString(tokenValue);
        });
        assertEquals(result.getEmail(), testMail);

        // expiredAt 시간이 현재 시간으로부터 30분 이후의 시간값이 생성되었는지 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resultExpiresAt = result.getExpiresAt();
        assertTrue(resultExpiresAt.isAfter(now) && resultExpiresAt.isBefore(now.plusMinutes(validityPeriod)));
        assertEquals(result.isResended(), false);
        assertEquals(result.isConfirmed(), false);
    }

    // createToken_failure() 생략
    // 컨트롤러 단에서 Java Bean Validation 기술을 사용한 유효성 검사를 수행하므로 검증 로직이 없음

    @Test
    @DisplayName("saveToken() : crudRepository save api 정상 호출")
    void saveToken_success() {
        String testMail = "new@test.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail).build();

        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenReturn(token);

        ConfirmationToken result = confirmationTokenService.saveToken(token);

        assertNotNull(result);
        assertEquals(result, token);
        verify(confirmationTokenRepository, times(1)).save(token);
    }

    @Test
    @DisplayName("sendEmail_success() : emailSender에서 sendEmail 메서드 한번 호출 확인")
    void sendEmail_success() {
        String recipientEmail = "test@example.com";
        String tokenValue = UUID.randomUUID().toString();
        String authLinkPath = String.format("/api/%s/signup/confirm", currentVersion);
        String authLink = origin + authLinkPath + "?token=" + tokenValue + "&email=" + recipientEmail;

        confirmationTokenService.sendEmail(recipientEmail, tokenValue);

        verify(emailSender, times(1)).send(MailType.AUTH, recipientEmail, authLink);
    }

    @Test
    @DisplayName("authenticateToken_success() : ConfirmationToken 인증 여부 확인")
    void authenticateToken_success() {
        String testMail = "test@example.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(validityPeriod)
                .build();
        String tokenValue = token.getToken();

        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.of(token));
        when(clock.now()).thenReturn(LocalDateTime.now());

        confirmationTokenService.authenticateToken(tokenValue);

        assertTrue(confirmationTokenService.getToken(anyString()).isPresent());
        assertEquals(token.isConfirmed(), true);
    }

    @Test
    @DisplayName("authenticateToken_failure_isExpired() : ConfirmationToken 만료됨")
    void authenticateToken_failure_isExpired() {
        String testMail = "test@example.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(validityPeriod)
                .build();
        String tokenValue = token.getToken();

        Random rand = new Random();
        long min = validityPeriod + 1;  // 범위를 넘어가는 최소값
        long max = 1440L;               // 24시간. expiredAt 값의 범위를 검증하는데 충분한 최대값
        long expirationMinutes = min + ((long)(rand.nextDouble()*(max - min)));

        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.of(token));

        // clock.now() 의 반환값이 유효기간 만료여부의 기준이 됨
        when(clock.now()).thenReturn(LocalDateTime.now().plusMinutes(expirationMinutes));

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            confirmationTokenService.authenticateToken(tokenValue);
        });
        assertTrue(confirmationTokenService.getToken(anyString()).isPresent());
        assertEquals(e.getMessage(), "token has expired");
        assertFalse(token.isConfirmed());
    }

    @Test
    @DisplayName("authenticateToken_failure_isConfirmed() : 이미 인증된 ConfirmationToken")
    void authenticateToken_failure_isConfirmed() {
        String testMail = "test@example.com";
        ConfirmationToken token = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(validityPeriod)
                .build();

        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.of(token));
        when(clock.now()).thenReturn(LocalDateTime.now());

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            String tokenValue = token.getToken();
            confirmationTokenService.authenticateToken(tokenValue);
        });
        assertTrue(confirmationTokenService.getToken(anyString()).isPresent());
        assertEquals(e.getMessage(), "token already confirmed");
        assertTrue(token.isConfirmed());
    }

    @Test
    @DisplayName("authenticateToken_failure_not_generated_by_server() : 서버로부터 생성되지 않은 ConfirmationToken")
    void authenticateToken_failure_not_generated_by_server() {
        String invalidToken = "invalid_token";

        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.empty());

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            confirmationTokenService.authenticateToken(invalidToken);
        });
        assertTrue(confirmationTokenService.getToken(anyString()).isEmpty());
        assertEquals(e.getMessage(), "token was not generated by the server");
    }
}
