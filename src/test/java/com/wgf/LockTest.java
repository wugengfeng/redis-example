package com.wgf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @description: 分布式锁测试
 * @author: ken 😃
 * @create: 2022-02-16 17:03
 **/
@SpringBootTest
public class LockTest {
    @Autowired
    RedisLockRegistry redisLockRegistry;

    @Test
    public void lockTest() throws InterruptedException {
        Lock lock = redisLockRegistry.obtain("test");

        for (int i = 0; i < 10; i++) {
            lock.lock();
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
