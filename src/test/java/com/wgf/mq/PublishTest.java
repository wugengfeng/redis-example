package com.wgf.mq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class PublishTest {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 发布消息
     */
    @Test
    public void publish() {
        redisTemplate.convertAndSend("myChannel", "hello!");
    }
}
