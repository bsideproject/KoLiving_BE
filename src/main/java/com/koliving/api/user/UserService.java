package com.koliving.api.user;

import com.koliving.api.event.ConfirmationTokenCreatedEvent;
import com.koliving.api.token.ConfirmationToken;
import com.koliving.api.token.IConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final IConfirmationTokenService confirmationTokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void saveTokenAndSendEmail(String mail) {
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .email(mail)
                .build();

        ConfirmationToken savedToken = confirmationTokenService.saveToken(confirmationToken);
        eventPublisher.publishEvent(new ConfirmationTokenCreatedEvent(savedToken));
    }
}
