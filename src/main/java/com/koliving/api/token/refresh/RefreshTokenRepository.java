package com.koliving.api.token.refresh;

import com.koliving.api.properties.JwtProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final static String RT_HASH_KEY = "RefreshToken";
    private static HashOperations<String, String, String> hashOperations;
    private final RedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public String findByEmail(String email) {
        return hashOperations.get(RT_HASH_KEY, email);
    }

    public String save(final RefreshToken refreshToken) {
        hashOperations.putIfAbsent(RT_HASH_KEY, refreshToken.email(), refreshToken.token());
        redisTemplate.expire(RT_HASH_KEY, jwtProperties.getRefreshValidity(), TimeUnit.DAYS);

        return refreshToken.token();
    }

    public boolean existByEmail(final String email) {
        return hashOperations.hasKey(RT_HASH_KEY, email);
    }

    public void delete(final String email) {
        hashOperations.delete(RT_HASH_KEY, email);
    }
}
