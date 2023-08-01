package com.koliving.api.token.confirmation;

import java.util.Optional;

public interface IConfirmationTokenService {

    Optional<ConfirmationToken> get(String token);

    ConfirmationToken create(String email);

    ConfirmationToken save(ConfirmationToken token);

    Long delete(String email);

    void sendEmail(String mail, String token);

    boolean authenticateToken(String token);
}
