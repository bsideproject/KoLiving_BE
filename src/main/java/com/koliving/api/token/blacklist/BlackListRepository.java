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

    private final RedisTemplate redisTemplate;
    private final static String BAT_HASH_KEY = "BlackAccessToken";
    private static HashOperations<String, String, Date> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void save(final BlackAccessToken blackAccessToken) {
        String tokenValue = blackAccessToken.getAccessToken();
        Date expirationDate = blackAccessToken.getExpirationTime();

        hashOperations.put(BAT_HASH_KEY, tokenValue, expirationDate);
        redisTemplate.expireAt(BAT_HASH_KEY, expirationDate);
    }
}
