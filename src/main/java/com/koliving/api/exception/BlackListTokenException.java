package com.koliving.api.exception;

import lombok.Getter;

@Getter
public class BlackListTokenException extends RuntimeException {

    public BlackListTokenException(String message) {
        super(message);
    }
}
