package com.koliving.api.token.refresh;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

    private String email;
    private String refreshToken;

    @Builder
    public RefreshToken(String email, String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;   // jwt
    }
}
