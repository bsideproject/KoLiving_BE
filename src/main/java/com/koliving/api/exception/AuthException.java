package com.koliving.api.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String email;

    public AuthException(String message, String email) {
        super(message);
        this.email = email;
    }
}
