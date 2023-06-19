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
        assertEquals(testMail, result.getEmail());
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
        String email = "test@example.com";
        String token = "testToken";
        String authLinkPath = String.format("/api/%s/signup/confirm", currentVersion);
        String authLink = origin + authLinkPath + "?token=" + token + "&email=" + email;

        confirmationTokenService.sendEmail(email, token);

        verify(emailSender).send(MailType.AUTH, email, authLink);
    }

    @Test
    @DisplayName("authenticateToken_success() : ConfirmationToken 인증 여부 확인")
    void authenticateToken_success() {
        String testMail = "test@example.com";
        ConfirmationToken newToken = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(validityPeriod)
                .build();

        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.of(newToken));
        when(clock.now()).thenReturn(LocalDateTime.now());

        String newTokenValue = newToken.getToken();
        confirmationTokenService.authenticateToken(newTokenValue);

        assertTrue(confirmationTokenService.getToken(anyString()).isPresent());
        assertEquals(newToken.isConfirmed(), true);
    }

    @Test
    @DisplayName("authenticateToken_failure_isExpired() : ConfirmationToken 만료됨")
    void authenticateToken_failure_isExpired() {
        String testMail = "test@example.com";
        ConfirmationToken newToken = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(validityPeriod)
                .build();
        String newTokenValue = newToken.getToken();

        Random rand = new Random();
        long min = validityPeriod + 1;
        long max = 1440L;
        long expirationMinutes = min + ((long)(rand.nextDouble()*(max - min)));

        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.of(newToken));
        when(clock.now()).thenReturn(LocalDateTime.now().plusMinutes(expirationMinutes));

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            confirmationTokenService.authenticateToken(newTokenValue);
        });
        assertTrue(confirmationTokenService.getToken(anyString()).isPresent());
        assertEquals(e.getMessage(), "token has expired");
        assertFalse(newToken.isConfirmed());
    }

    @Test
    @DisplayName("authenticateToken_failure_isConfirmed() : 이미 인증된 ConfirmationToken")
    void authenticateToken_failure_isConfirmed() {
        String testMail = "test@example.com";
        ConfirmationToken newToken = ConfirmationToken.builder()
                .email(testMail)
                .validityPeriod(validityPeriod)
                .build();
        String newTokenValue = newToken.getToken();
        newToken.confirm();

        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.of(newToken));
        when(clock.now()).thenReturn(LocalDateTime.now());

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            confirmationTokenService.authenticateToken(newTokenValue);
        });
        assertTrue(confirmationTokenService.getToken(anyString()).isPresent());
        assertEquals(e.getMessage(), "token already confirmed");
        assertTrue(newToken.isConfirmed());
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
