package com.koliving.api.dto;

import com.koliving.api.annotation.PasswordConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 - 비밀번호 등록")
public record PasswordDto(
        @NotBlank
        @Size(min = 6, max = 30)
        @PasswordConstraint
        @Schema(description = "비밀번호 (소문자, 숫자, 특수기호 포함 필수)", minLength = 6, maxLength = 30, example = "koliving1!")
        String password
        ) {
}
