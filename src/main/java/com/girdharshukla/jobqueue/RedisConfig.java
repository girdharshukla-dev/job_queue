package com.girdharshukla.jobqueue;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.StatefulRedisConnection;

@Configuration
public class RedisConfig {
    @Bean
    @Primary
    public RedisCommands<String, String> redisCommands(@Value("${spring.redis.url}") String redisUri) {
        RedisClient client = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();
        return commands;
    }

    @Bean("workerCommands")
    public RedisCommands<String, String> workerRedisCommands(@Value("${spring.redis.url}") String uri) {
        RedisURI redisUri = RedisURI.create(uri);
        redisUri.setTimeout(Duration.ZERO);
        RedisClient client = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();
        return commands;
    }

}
