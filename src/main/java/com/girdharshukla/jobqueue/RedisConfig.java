package com.girdharshukla.jobqueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.StatefulRedisConnection;

@Configuration
public class RedisConfig {
    @Bean
    public RedisCommands<String, String> redisCommands(@Value("${spring.redis.url}") String redisUri){
        RedisClient client = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();
        return commands;
    }
}
