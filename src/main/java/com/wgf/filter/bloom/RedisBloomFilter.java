package com.wgf.filter.bloom;

import com.wgf.filter.command.BloomCommand;
import com.wgf.filter.command.CommandHelper;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;

/**
 * @description: Redis插件布隆过滤器
 * @author: ken 😃
 * @create: 2022-02-22 12:24
 **/
public class RedisBloomFilter {

    private final RedisTemplate redisTemplate;
    private final List<String>  key;

    public RedisBloomFilter(RedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key           = Collections.singletonList(key);
    }

    /**
     * 创建布隆过滤器
     *
     * @param errorRate    误报的期望概率 比如 "0.0001"
     * @param capacity     容量
     * @param milliseconds 过期时间，单位毫秒。-1为用不过期
     * @return
     */
    public boolean reserve(double errorRate, int capacity, int milliseconds) {
        return (boolean) redisTemplate.execute(BloomCommand.getReserveScript(), key, errorRate, capacity, milliseconds);
    }


    /**
     * 添加item到布隆过滤器，不存在则创建布隆过滤器
     *
     * @param item
     * @return
     */
    public boolean add(Object item) {
        return (boolean) redisTemplate.execute(BloomCommand.getAddScript(), key, item);
    }

    /**
     * 批量添加item到布隆过滤器，不存在则创建布隆过滤器
     *
     * @param items
     * @return 返回添加成功的item
     */
    public List<Object> multiAdd(List<Object> items) {
        List<Long> result = (List<Long>) redisTemplate.execute(BloomCommand.getMultiAdd(items), key, items.toArray());

        return CommandHelper.extract(result, items);
    }

    /**
     * 判断item是否存在
     * @param item
     * @return
     */
    public boolean exists(Object item) {
        return (boolean) redisTemplate.execute(BloomCommand.getExistsScript(), key, item);
    }

    /**
     * 批量判断items是否存在
     * @param items
     * @return 返回存在的item
     */
    public List<Object> multiExists(List<Object> items) {
        List<Long> result = (List<Long>) redisTemplate.execute(BloomCommand.getMultiExists(items), key, items.toArray());
        return CommandHelper.extract(result, items);
    }
}
