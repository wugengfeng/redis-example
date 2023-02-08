package com.wgf.conf;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * @author: ken 😃
 * @date: 2023-02-08
 * @description:
 * 支持过期的Redis缓存管理器
 **/
public class TtlRedisCacheManager extends RedisCacheManager {
    public TtlRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        // 缓存名使用 # 分隔，前面是缓存名称，后面是TTL过期时间
        String[] array = StringUtils.delimitedListToStringArray(name, "#");

        // 解析TTL
        if (array.length > 1) {
            long ttl = Long.parseLong(array[1]);
            cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(ttl)); // 单位毫秒
        }
        return super.createRedisCache(name, cacheConfig);
    }
}
