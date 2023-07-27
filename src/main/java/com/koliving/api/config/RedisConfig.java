package com.koliving.api.config;

import com.koliving.api.properties.RedisProperties;
import lombok.AllArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@AllArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedisConnectionFactory connectionFactory(RedissonClient redisClient) {
        return new RedissonConnectionFactory(redisClient);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedissonClient redisClient) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory(redisClient));
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() throws IOException {
        Config config;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/redisson.yaml")) {
            config = Config.fromYAML(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Redisson configuration.", e);
        }

        SingleServerConfig singleServerConfig = config.useSingleServer();

        String address = REDISSON_HOST_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort();
        singleServerConfig.setAddress(address);
        // TODO config: setPassword

        return Redisson.create(config);
    }
}
