package com.wgf;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * BitMap 测试
 * @author: ken 😃
 * @date: 2023-01-29
 * @description:
 **/
@Slf4j
@SpringBootTest
public class BitMapTest {
    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void setBitTest() {
        redisTemplate.opsForValue().setBit("wgf", 0 ,true);
        redisTemplate.opsForValue().setBit("wgf", 3 ,true);
        redisTemplate.opsForValue().setBit("wgf", 5 ,true);
        redisTemplate.opsForValue().setBit("wgf",6 ,true);
    }


    @Test
    public void getBitTest() {
        log.info("value: {}", redisTemplate.opsForValue().getBit("wgf", 0));
    }


    @Test
    public void bitCountTest() {
        Long num = (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount("wgf".getBytes());
            }
        });

        log.info("num: {}", num);
    }


    @Test
    public void bitPosTest() {
        Long value = (Long) redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitPos("wgf".getBytes(), false));
        log.info("value: {}", value);
    }
}
