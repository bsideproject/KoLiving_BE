package com.koliving.api.user;

import com.koliving.api.event.ConfirmationTokenCreatedEvent;
import com.koliving.api.token.confirmation.ConfirmationToken;
import com.koliving.api.token.confirmation.IConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthFacade {
    private final IConfirmationTokenService confirmationTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public void processEmailAuth(String email) {
        ConfirmationToken newToken = confirmationTokenService.createToken(email);
        ConfirmationToken savedToken = confirmationTokenService.saveToken(newToken);
        eventPublisher.publishEvent(new ConfirmationTokenCreatedEvent(savedToken));
    }


}
