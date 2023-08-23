package com.koliving.api.exception;

import lombok.Getter;

@Getter
public class ConfirmationTokenException extends RuntimeException {

    private final String email;
    private final String token;

    public ConfirmationTokenException(String message, String email, String token) {
        super(message);
        this.email = email;
        this.token = token;
    }
}
