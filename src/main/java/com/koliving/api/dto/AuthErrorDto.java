package com.koliving.api.dto;

public record AuthErrorDto(
        String errorMessage,
        String email) {
}
