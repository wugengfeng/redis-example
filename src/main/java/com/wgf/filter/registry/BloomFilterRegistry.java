package com.wgf.filter.registry;

import com.wgf.filter.bloom.RedisBloomFilter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 布隆过滤器注册器
 * @author: ken 😃
 * @create: 2022-02-22 12:14
 **/
public class BloomFilterRegistry {
    private final static String                        PREFIX    = "bloom:";
    private final        Map<String, RedisBloomFilter> container = new ConcurrentHashMap<>(8);

    private RedisTemplate redisTemplate;

    public BloomFilterRegistry(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisBloomFilter getFilter(String key) {
        String name = getName(key);
        return container.computeIfAbsent(name, line -> new RedisBloomFilter(redisTemplate, line));
    }

    private String getName(String key) {
        return String.format("%s%s", PREFIX, key);
    }
}
