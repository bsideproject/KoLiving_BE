package com.koliving.api.token.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private final RedisTemplate redisTemplate;
    private final long RT_EXPIRATION_TIME;
    private final static String RT_HASH_KEY = "RefreshToken";

    public RefreshTokenRepository(RedisTemplate redisTemplate,
                                  @Value("${jwt.refreshExpiration:30}") long RT_EXPIRATION_TIME) {
        this.redisTemplate = redisTemplate;
        this.RT_EXPIRATION_TIME = RT_EXPIRATION_TIME;
    }

    public String save(final RefreshToken refreshToken) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.putIfAbsent(RT_HASH_KEY, refreshToken.getEmail(), refreshToken.getRefreshToken());

        redisTemplate.expire(RT_HASH_KEY, RT_EXPIRATION_TIME, TimeUnit.DAYS);

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
