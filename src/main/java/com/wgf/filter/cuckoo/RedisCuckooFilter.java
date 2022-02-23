package com.wgf.filter.cuckoo;

import com.wgf.filter.command.CuckooCommand;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;

/**
 * @description: 布谷鸟过滤器
 * @author: ken 😃
 * @create: 2022-02-22 18:30
 **/
public class RedisCuckooFilter {
    private final RedisTemplate redisTemplate;
    private final List<String>  key;

    public RedisCuckooFilter(RedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key           = Collections.singletonList(key);
    }

    /**
     * 创建布谷鸟过滤器
     *
     * @param capacity     容量
     * @param milliseconds 过期时间，单位毫秒。-1为用不过期
     * @return
     */
    public boolean reserve(int capacity, int milliseconds) {
        return (boolean) redisTemplate.execute(CuckooCommand.getReserveScript(), key, capacity, milliseconds);
    }

    /**
     * 添加item到布谷鸟过滤器，不存在则创建布隆过滤器
     *
     * @param item
     * @return
     */
    public boolean add(Object item) {
        return (boolean) redisTemplate.execute(CuckooCommand.getAddScript(), key, item);
    }

    /**
     * 如果item不存在，添加item到布谷鸟过滤器
     *
     * @param item
     * @return
     */
    public boolean addNx(Object item) {
        return (boolean) redisTemplate.execute(CuckooCommand.getAddNxScript(), key, item);
    }

    /**
     * 判断item是否存在
     *
     * @param item
     * @return
     */
    public boolean exists(Object item) {
        return (boolean) redisTemplate.execute(CuckooCommand.getExistsScript(), key, item);
    }

    /**
     * 删除item
     *
     * @param item
     * @return
     */
    public boolean del(Object item) {
        return (boolean) redisTemplate.execute(CuckooCommand.getDelScript(), key, item);
    }
}
