package com.koliving.api.token.refresh;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash(value = "RefreshToken", timeToLive = 60*60*24*14) // 2week
@Getter
public class RefreshToken {

    @Id
    private String email;
    private String refreshToken;

    @Builder
    public RefreshToken(String email) {
        this.email = email;
        this.refreshToken = UUID.randomUUID().toString();
    }
}
