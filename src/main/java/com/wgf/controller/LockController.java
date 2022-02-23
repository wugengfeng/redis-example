package com.wgf.controller;

import com.wgf.lock.MyRedisLock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * @description: 分布式锁测试
 * @author: ken 😃
 * @create: 2022-02-16 10:52
 **/
@Slf4j
@Api(tags = "分布式锁API")
@RestController
public class LockController {
    public static int NUM = 100000;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Autowired
    private MyRedisLock myRedisLock;

    @ApiOperation(value = "分布式锁")
    @GetMapping("lock")
    public int lock() {
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        Runnable       task           = new Task(countDownLatch);

        for (int i = 0; i < 1000; i++) {
            executorService.execute(task);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return NUM;
    }

    class Task implements Runnable {
        private CountDownLatch latch;

        public Task(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            // 获取锁对象
            Lock    lock    = redisLockRegistry.obtain("lock");
            boolean lockRes = false;

            try {
                // 尝试获取锁，10秒超时
                //lockRes = lock.tryLock(10, TimeUnit.SECONDS);

                // 死等
                lock.lock();
                log.info("获取锁成功");
                NUM--;
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                lock.unlock();
            }
        }
    }


    @ApiOperation(value = "自定义分布式锁")
    @GetMapping("my_lock")
    public int myLock() {
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        Runnable       task           = new MyLockTask(countDownLatch);

        for (int i = 0; i < 1000; i++) {
            executorService.execute(task);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return NUM;
    }

    class MyLockTask implements Runnable {
        private CountDownLatch latch;

        public MyLockTask(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                myRedisLock.lock("test", 10 * 1000);
                log.info("获取锁成功");
                NUM--;
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                myRedisLock.unlock("test");
            }
        }
    }
}
