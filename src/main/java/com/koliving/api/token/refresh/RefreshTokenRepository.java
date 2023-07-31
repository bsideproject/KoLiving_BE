package com.koliving.api.token.refresh;

import jakarta.annotation.PostConstruct;
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
    private static HashOperations<String, String, String> hashOperations;

    public RefreshTokenRepository(RedisTemplate redisTemplate,
                                  @Value("${jwt.refreshExpiration:30}") long RT_EXPIRATION_TIME) {
        this.redisTemplate = redisTemplate;
        this.RT_EXPIRATION_TIME = RT_EXPIRATION_TIME;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public String findByEmail(String email) {
        return hashOperations.get(RT_HASH_KEY, email);
    }

    public String save(final RefreshToken refreshToken) {
        hashOperations.putIfAbsent(RT_HASH_KEY, refreshToken.email(), refreshToken.refreshToken());
        redisTemplate.expire(RT_HASH_KEY, RT_EXPIRATION_TIME, TimeUnit.DAYS);

        return refreshToken.refreshToken();
    }

    public boolean existByEmail(final String email) {
        return hashOperations.hasKey(RT_HASH_KEY, email);
    }

    public void delete(final String email) {
        hashOperations.delete(RT_HASH_KEY, email);
    }
}
