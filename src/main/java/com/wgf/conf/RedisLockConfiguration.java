package com.wgf.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @description: 分布式锁配置
 * @author: ken 😃
 * @create: 2022-02-16 10:36
 **/
@Configuration
public class RedisLockConfiguration {

    /**
     * 锁前缀
     */
    private final static String LOCK_NAME = "redis-lock";

    /**
     * 锁过期时间
     */
    private final static Long EXPIRE = 20000L;

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, LOCK_NAME, EXPIRE);
    }
}
