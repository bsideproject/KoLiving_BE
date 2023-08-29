package com.koliving.api.exception;

public class NonExistentResourceException extends RuntimeException {
    public NonExistentResourceException(String message) {
        super(message);
    }
}
