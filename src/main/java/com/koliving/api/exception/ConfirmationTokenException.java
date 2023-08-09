package com.koliving.api.exception;

import lombok.Getter;

@Getter
public class ConfirmationTokenException extends RuntimeException {

    private final String email;

    public ConfirmationTokenException(String message, String email) {
        super(message);
        this.email = email;
    }
}
