package com.koliving.api.token.confirmation;

import lombok.Getter;

@Getter
public enum ConfirmationTokenType {
    SIGN_UP("auth/sign-up/confirm"), RESET_PASSWORD("auth/reset-password/confirm");

    private final String redirectResourcePath;

    ConfirmationTokenType(String redirectResourcePath) {
        this.redirectResourcePath = redirectResourcePath;
    }
}
