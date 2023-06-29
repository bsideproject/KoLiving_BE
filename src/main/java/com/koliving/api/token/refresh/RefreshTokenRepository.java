package com.koliving.api.token.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate redisTemplate;
    private static final String RT_HASH_KEY = "RefreshToken";

    public String save(final RefreshToken refreshToken) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.putIfAbsent(RT_HASH_KEY, refreshToken.getEmail(), refreshToken.getRefreshToken());

        redisTemplate.expire(RT_HASH_KEY, 30L, TimeUnit.DAYS);

        return refreshToken.getRefreshToken();
    }

    public boolean existByEmail(final String email) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.hasKey(RT_HASH_KEY, email);
    }

    public void delete(final String email) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(RT_HASH_KEY, email);
    }
}
