package com.koliving.api.token;

import java.util.Optional;

public interface IConfirmationTokenService {

    Optional<ConfirmationToken> getToken(String token);

    ConfirmationToken createToken(String email);

    ConfirmationToken saveToken(ConfirmationToken token);

    void sendEmail(String mail, String token);

    String authenticateToken(String token);
}
