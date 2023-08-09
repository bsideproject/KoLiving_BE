package com.koliving.api.dto;

public record ConfirmationTokenErrorDto(
        String errorMessage,
        String email) {
}
