package com.wgf.conf;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * @author: ken 😃
 * @date: 2023-02-08
 * @description:
 **/
@EnableCaching
@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // redis 配置
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .computePrefixWith(cacheName -> "caching:" + cacheName);

        TtlRedisCacheManager redisCacheManager = new TtlRedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory), defaultCacheConfig);
        return redisCacheManager;
    }
}
