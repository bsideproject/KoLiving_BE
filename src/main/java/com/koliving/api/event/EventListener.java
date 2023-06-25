package com.koliving.api.event;

import com.koliving.api.token.confirmation.IConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class EventListener {

    private final IConfirmationTokenService confirmationTokenService;

    @Async
    @TransactionalEventListener(
        classes = ConfirmationTokenCreatedEvent.class,
        phase = TransactionPhase.AFTER_COMMIT
    )
    public void onConfirmationTokenCreated(ConfirmationTokenCreatedEvent event) {
        confirmationTokenService.sendEmail(event.getEmail(), event.getToken());
    }
}
