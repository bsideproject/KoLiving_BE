package com.koliving.api.dto;

import com.koliving.api.annotation.PasswordConstraint;
import com.koliving.api.annotation.PasswordMatches;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 - 비밀번호 재설정")
@PasswordMatches
public record ResetPasswordDto(
        @Schema(description = "비밀번호 (문자, 숫자 포함. 특수문자 선택)", minLength = 6, maxLength = 30, example = "Koliving1")
        @NotBlank
        @Size(min = 6, max = 30)
        @PasswordConstraint
        String password,

        @Schema(description = "비밀번호 (문자, 숫자 포함. 특수문자 선택)", minLength = 6, maxLength = 30, example = "Koliving1")
        String passwordVerify
        ) {
}
