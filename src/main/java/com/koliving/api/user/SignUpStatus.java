package com.koliving.api.user;

import lombok.Getter;

@Getter
public enum SignUpStatus {

    PASSWORD_VERIFICATION_PENDING("/signup/step2"),
    PROFILE_INFORMATION_PENDING("/signup/step3"),
    COMPLETED("/login");

    private final String redirectUrl;

    SignUpStatus(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
