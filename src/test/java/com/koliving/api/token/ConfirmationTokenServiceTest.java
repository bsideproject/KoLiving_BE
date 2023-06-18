package com.koliving.api.token;

import com.koliving.api.clock.IClock;
import com.koliving.api.email.IEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    IEmailService emailService;

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
                emailService,
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

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(token.getToken());

        assertTrue(result.isPresent());
        assertEquals(tokenValue, result.get().getToken());

        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
    }

    @Test
    @DisplayName("getToken() : token 값에 따른 행이 없음")
    void getToken_failure() {

        // 생성자 함수를 통해 token 값이 UUID.randomUUID().toString()의 응답값으로 할당됨
        // 문자열만 구성된 token 값은 존재하지 않음
        String nonExistentTokenValue = "invalid token value";
        String tokenValue = nonExistentTokenValue;

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(tokenValue);

        assertTrue(result.isEmpty());

        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
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
    void sendEmail() {
    }

    @Test
    void authenticateToken() {
    }
}
