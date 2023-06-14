package com.koliving.api.registeration.token;

import com.koliving.api.email.IEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfirmationTokenService implements IConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final IEmailService emailSender;
    private String origin;
    private String currentVersion;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository,
                                    IEmailService emailSender,
                                    @Value("${server.origin:http://localhost:8080}") String origin,
                                    @Value("${server.current-version:v1}") String currentVersion
                                    ) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailSender = emailSender;
        this.origin = origin;
        this.currentVersion = currentVersion;
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        return Optional.empty();
    }

    @Override
    public ConfirmationToken saveToken(ConfirmationToken token) {
        return null;
    }

    @Override
    public void sendEmail(String mail, String token) {}

    @Override
    public String authenticateToken(String token) {
        return null;
    }
}
