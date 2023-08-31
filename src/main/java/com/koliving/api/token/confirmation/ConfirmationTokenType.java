package com.koliving.api.token.confirmation;

import lombok.Getter;

@Getter
public enum ConfirmationTokenType {
    SIGN_UP("auth/sign-up/confirm", "signup/step2"),
    RESET_PASSWORD("auth/reset-password/confirm", "resetPassword/step2");

    private final String linkPathResource;
    private final String redirectPath;

    ConfirmationTokenType(String linkPathResource, String redirectPath) {
        this.linkPathResource = linkPathResource;
        this.redirectPath = redirectPath;
    }
}
