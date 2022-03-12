package com.example.hanghaefinal.config.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

// 내장 레디스를 사용하기 위한 코드
// local 환경에서 여타의 CRUD를 테스트해보기 위해서는 Profile 을 local 로 설정해주어야 한다
// 깃이그노어 반영 확인
//@Profile("local")
@Profile("52.78.138.53")
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

}