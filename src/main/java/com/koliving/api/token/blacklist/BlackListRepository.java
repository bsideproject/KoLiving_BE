package com.koliving.api.token.blacklist;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@RequiredArgsConstructor
public class BlackListRepository {

    private final static String BAT_HASH_KEY = "BlackAccessToken";
    private static HashOperations<String, String, Date> hashOperations;
    private final RedisTemplate redisTemplate;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void save(final BlackAccessToken blackAccessToken) {
        hashOperations.put(BAT_HASH_KEY, blackAccessToken.accessToken(), blackAccessToken.expirationTime());
        redisTemplate.expireAt(BAT_HASH_KEY, blackAccessToken.expirationTime());
    }

    public boolean existByToken(final String token) {
        return hashOperations.hasKey(BAT_HASH_KEY, token);
    }
}
