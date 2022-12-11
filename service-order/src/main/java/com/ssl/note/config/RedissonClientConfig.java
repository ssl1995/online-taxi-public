package com.ssl.note.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonClientConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Value("${spring.redis.database}")
    private Integer redisDatabase;

    public static final String REDIS_PREFIX = "redis://";

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();

        config.useSingleServer()
                .setAddress(REDIS_PREFIX + redisHost + ":" + redisPort)
                .setPassword(redisPassword)
                .setDatabase(redisDatabase);

        return Redisson.create(config);
    }
}