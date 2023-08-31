package com.koliving.api.user;

import com.koliving.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public enum SignUpStatus {

    PASSWORD_VERIFICATION_PENDING("/signup/step2"),
    PROFILE_INFORMATION_PENDING("/signup/step2"),
    COMPLETED("/login");

    private final String redirectPath;

    SignUpStatus(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public String getRedirectUri(HttpUtils httpUtils, HttpServletRequest request) {
        if (this == PASSWORD_VERIFICATION_PENDING || this == PROFILE_INFORMATION_PENDING) {
            return httpUtils.getRedirectUri(request, getRedirectPath());
        } else if (this == COMPLETED) {
            return httpUtils.getCurrentVersionPath(getRedirectPath());
        }

        return null;
    }
}
