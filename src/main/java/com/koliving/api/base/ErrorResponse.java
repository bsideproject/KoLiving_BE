package com.koliving.api.base;

public record ErrorResponse(String code, String message) {
    public static ErrorResponse valueOf(ServiceError error) {
        return new ErrorResponse(error.getCode(), error.getMessage());
    }
}
