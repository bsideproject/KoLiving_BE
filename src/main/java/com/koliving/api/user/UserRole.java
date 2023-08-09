package com.koliving.api.user;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String securityName;

    UserRole(String securityName) {
        this.securityName = securityName;
    }
}
