package com.wgf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @description:
 * @author: ken 😃
 * @create: 2022-02-15 15:29
 **/
@SpringBootTest
public class ClusterTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void test() {
        for (int i = 1; i <= 100; i++) {
            String key = "k_" + i;
            String value = "v_" + i;
            redisTemplate.opsForValue().set(key, value);
        }
    }
}
