package com.koliving.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증 요청")
public record AuthEmailRequestDto(
        @NotBlank
        @Email
        @Schema(description = "이메일", example = "koliving@gmail.com")
        String email
) {
}
