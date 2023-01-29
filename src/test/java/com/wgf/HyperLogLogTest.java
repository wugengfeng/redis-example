package com.wgf;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * HyperLogLog 测试
 *
 * @author: ken 😃
 * @date: 2023-01-29
 * @description:
 **/
@Slf4j
@SpringBootTest
public class HyperLogLogTest {
    @Autowired
    RedisTemplate redisTemplate;

    private static String KEY = "pv";

    @Test
    public void pfAddTest() {
        for (int i = 0; i < 1000; i++) {
            redisTemplate.opsForHyperLogLog().add(KEY, i);
        }
    }

    @Test
    public void pfCountTest() {
        log.info("count: {}", redisTemplate.opsForHyperLogLog().size(KEY));
    }
}
