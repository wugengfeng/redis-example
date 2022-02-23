package com.wgf;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @description: 哨兵测试
 * @author: ken 😃
 * @create: 2022-02-12 12:00
 **/
@Slf4j
@SpringBootTest
public class SentinelTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void setTest() {
        redisTemplate.opsForValue().set("test", "test");
    }

    @Test
    public void getTest() {
      log.info("test = {}", redisTemplate.opsForValue().get("test"));
    }
}
