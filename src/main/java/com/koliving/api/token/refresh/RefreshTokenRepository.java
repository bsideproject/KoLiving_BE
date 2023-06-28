package com.koliving.api.token.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private RedisTemplate redisTemplate;

    public String save(final RefreshToken refreshToken) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String id = "RefreshToken";

        hashOperations.putIfAbsent(id, refreshToken.getEmail(), refreshToken.getRefreshToken());

        redisTemplate.expire(id, 30L, TimeUnit.DAYS);

        return refreshToken.getRefreshToken();
    }

    public boolean existByEmail(final String email) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String id = "RefreshToken";

        return hashOperations.hasKey(id, email);
    }

    public void delete(final String email) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String id = "RefreshToken";

        hashOperations.delete(id, email);
    }
}
