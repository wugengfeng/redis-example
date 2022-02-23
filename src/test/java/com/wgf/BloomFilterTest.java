package com.wgf;

import com.wgf.filter.bloom.RedisBloomFilter;
import com.wgf.filter.registry.BloomFilterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @description: BloomFilter过滤器测试
 * @author: ken 😃
 * @create: 2022-02-21 18:25
 **/
@Slf4j
@SpringBootTest
public class BloomFilterTest {
    @Autowired
    private BloomFilterRegistry bloomFilterRegistry;

    private static final String filterName = "bloom-filter";

    private RedisBloomFilter redisBloomFilter;

    @BeforeEach
    public void init() {
        redisBloomFilter = bloomFilterRegistry.getFilter(filterName);
    }

    /**
     * 创建布隆过滤器
     */
    @Test
    public void reserve() {
        boolean result = redisBloomFilter.reserve(0.001, 10000, -1);
        log.info("result : {}", result);
    }

    /**
     * 添加数据到布隆过滤器
     */
    @Test
    public void add() {
        boolean result = redisBloomFilter.add(1);
        log.info("result : {}", result);
    }

    /**
     * 批量添加数据到布隆过滤器
     */
    @Test
    public void multiAdd() {
        List<Object> values = Arrays.asList(2, 3, "test");
        List<Object> result = redisBloomFilter.multiAdd(values);
        log.info("result : {}", result);
    }


    /**
     * 判断数据是否存在
     */
    @Test
    public void exists() {
        boolean result = redisBloomFilter.exists("test");
        log.info("result : {}", result);
    }

    /**
     * 批量判断数据是否存在
     */
    @Test
    public void multiExists() {
        List<Object> values = Arrays.asList(1, 3, 5, "test");
        List<Object> result = redisBloomFilter.multiExists(values);
        log.info("result : {}", result);
    }
}
