package com.wgf.filter.registry;

import com.wgf.filter.cuckoo.RedisCuckooFilter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 布谷鸟过滤器注册器
 * @author: ken 😃
 * @create: 2022-02-22 18:51
 **/
public class CuckooFilterRegistry {
    private final static String                         PREFIX    = "cuckoo:";
    private final        Map<String, RedisCuckooFilter> container = new ConcurrentHashMap<>(8);
    private              RedisTemplate                  redisTemplate;

    public CuckooFilterRegistry(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisCuckooFilter getFilter(String key) {
        String name = getName(key);
        return container.computeIfAbsent(name, line -> new RedisCuckooFilter(redisTemplate, line));
    }

    private String getName(String key) {
        return String.format("%s%s", PREFIX, key);
    }
}
