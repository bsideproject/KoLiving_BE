package com.koliving.api.token;

import com.koliving.api.clock.IClock;
import com.koliving.api.email.IEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void getToken() {
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
