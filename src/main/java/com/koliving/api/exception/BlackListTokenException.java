package com.koliving.api.exception;

import lombok.Getter;

@Getter
public class BlackListTokenException extends RuntimeException {

    private final String email;

    public BlackListTokenException(String email) {
        super("black_list_token:" + email);
        this.email = email;
    }
}
