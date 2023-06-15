package com.koliving.api.registeration.token;

import java.util.Optional;

public interface IConfirmationTokenService {

    Optional<ConfirmationToken> getToken(String token);

    ConfirmationToken saveToken(ConfirmationToken token);

    void sendEmail(String mail, String token);

    String authenticateToken(String token);
}
