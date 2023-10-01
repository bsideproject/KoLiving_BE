package com.koliving.api.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * author : haedoang date : 2023/10/01 description :
 */
@Schema(description = "토큰 발급 정보")
public record TokenResponse(
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiI0IiwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6Im1lbWJlcjp3cml0ZSJ9LHsiYXV0aG9yaXR5IjoibWVtYmVyOnJlYWQifSx7ImF1dGhvcml0eSI6InJvb206cmVhZCJ9LHsiYXV0aG9yaXR5Ijoicm9vbTp3cml0ZSJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjU4MjE4MTI2LCJleHAiOjE2ODk2OTI0MDB9.9S32yrsYqbnLQZGP4kPnoK3K5M1o2RmTzF-rbj8ABqFCk95tB-8irPCArdptIUp_")
    String accessToken
) {

    public static TokenResponse valueOf(String accessToken) {
        return new TokenResponse(accessToken);
    }

}
