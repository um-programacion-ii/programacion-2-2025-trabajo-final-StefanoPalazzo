package com.stefanopalazzo.eventosbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {

    @Value("${spring.session.redis.host:localhost}")
    private String sessionRedisHost;

    @Value("${spring.session.redis.port:6380}")
    private int sessionRedisPort;

    @Bean
    public LettuceConnectionFactory sessionRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(sessionRedisHost, sessionRedisPort);
        return new LettuceConnectionFactory(config);
    }
}
