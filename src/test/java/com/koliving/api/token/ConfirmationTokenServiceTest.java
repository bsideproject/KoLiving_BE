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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void createToken() {
    }

    @Test
    void saveToken() {
    }

    @Test
    void sendEmail() {
    }

    @Test
    void authenticateToken() {
    }
}
