package com.koliving.api.token.confirmation;

import com.koliving.api.clock.IClock;
import com.koliving.api.email.IEmailService;
import com.koliving.api.email.MailType;
import com.koliving.api.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenService implements IConfirmationTokenService {

    private final IEmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final IClock clock;
    private final HttpUtils httpUtils;
    private final long validityPeriod;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository,
                                    IEmailService emailService,
                                    IClock clock,
                                    HttpUtils httpUtils,
                                    @Value("${spring.mail.properties.mail.auth.validity-period:30}") long validityPeriod
                                    ) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
        this.clock = clock;
        this.httpUtils = httpUtils;
        this.validityPeriod = validityPeriod;
    }

    @Override
    public Optional<ConfirmationToken> get(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public ConfirmationToken create(String email) {
        return ConfirmationToken.builder()
                .email(email)
                .validityPeriod(validityPeriod)
                .build();
    }

    @Override
    @Transactional
    public ConfirmationToken save(ConfirmationToken token) {
        return confirmationTokenRepository.save(token);
    }

    @Override
    public Long delete(String token) {
        return confirmationTokenRepository.deleteByToken(token);
    }

    @Override
    public void sendEmail(String email, String token) {
        String authLinkPath = httpUtils.getCurrentVersionUrl("auth/sign-up/confirm");
        String authLinkQueryString = "?token=" + token + "&email=" + email;
        String authLinkurl = authLinkPath + authLinkQueryString;

        emailService.send(MailType.AUTH, email, authLinkurl);
    }

    @Override
    @Transactional
    public boolean authenticateToken(String token) {
        Optional<ConfirmationToken> optionalConfirmationToken = this.get(token);
        if (optionalConfirmationToken.isEmpty()) {
            this.handleInvalidToken();
        }

        optionalConfirmationToken
                .filter(this::isNotExpired)
                .filter(this::isNotConfirmed)
                .ifPresent(this::confirmToken);

        return true;
    }

    private boolean isNotExpired(ConfirmationToken confirmationToken) {
        LocalDateTime expiresAt = confirmationToken.getExpiredDate();
        if (isExpired(expiresAt)) {
            throw new IllegalArgumentException("expired_confirmation_token");
        }

        return true;
    }

    private boolean isNotConfirmed(ConfirmationToken confirmationToken) {
        if (confirmationToken.isBConfirmed()) {
            throw new IllegalStateException("authenticated_confirmation_token");
        }

        return true;
    }

    private void handleInvalidToken() {
        throw new IllegalArgumentException("ungenerated_confirmation_token");
    }

    private boolean isExpired(LocalDateTime expiredAt) {
        LocalDateTime now = clock.now();
        return now.isAfter(expiredAt);
    }

    private void confirmToken(ConfirmationToken confirmationToken) {
        confirmationToken.confirm();
    }
}
