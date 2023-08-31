package com.koliving.api.user;

import com.koliving.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public enum SignUpStatus {

    PASSWORD_VERIFICATION_PENDING("/password"), PROFILE_INFORMATION_PENDING("/profile"), COMPLETED("/login");

    private final String redirectResourcePath;

    SignUpStatus(String redirectResourcePath) {
        this.redirectResourcePath = redirectResourcePath;
    }

    public String getRedirectUri(HttpUtils httpUtils, HttpServletRequest request) {
        if (this == PASSWORD_VERIFICATION_PENDING || this == PROFILE_INFORMATION_PENDING) {
            return httpUtils.getRedirectUri(request, getRedirectResourcePath());
        } else if (this == COMPLETED) {
            return httpUtils.getCurrentVersionPath(getRedirectResourcePath());
        }

        return null;
    }
}
