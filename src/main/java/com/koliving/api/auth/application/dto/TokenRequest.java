package com.koliving.api.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
@Schema(description = "토큰 발급 요청")
public record TokenRequest(
    @Schema(description = "계정", example = "koliving@koliving.com")
    @NotEmpty String email,

    @Schema(description = "비밀번호", example = "test1234!@")
    @NotEmpty String password
) {

}
