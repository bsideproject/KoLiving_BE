package com.koliving.api.token.refresh;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "RefreshToken", timeToLive = 60*60*24*14) // 2week
public class RefreshToken {

    @Id
    private String email;
    private String refreshToken;

    @Builder
    public RefreshToken(String email, String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;   // jwt
    }
}
