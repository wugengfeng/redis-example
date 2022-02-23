package com.wgf;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: ken 😃
 * @create: 2022-02-08 16:47
 **/
@Slf4j
@SpringBootTest
public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void look() {
        redisTemplate.opsForValue().setIfAbsent("test", "test", 20, TimeUnit.SECONDS);
    }


    @Test
    public void decrBy() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        Runnable task = () -> {
            Long stock = redisTemplate.opsForValue().decrement("stock");

            if (stock < 0) {
                log.error("error");
                redisTemplate.opsForValue().increment("stock");
            } else {
                log.info("库存成功扣减");
            }
        };

        for (int i = 0; i < 1000; i++) {
            executorService.execute(task);
        }

        TimeUnit.SECONDS.sleep(60);
    }

}
