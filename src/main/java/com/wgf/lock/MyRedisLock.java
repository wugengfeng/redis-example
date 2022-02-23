package com.wgf.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description: 自定义极简化分布式锁
 * @author: ken 😃
 * @create: 2022-02-16 17:41
 **/
@Component
public class MyRedisLock {
    private final String PREFIX = "redis-lock:";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取锁
     *
     * @param lockName    锁名称
     * @param lockTimeOut 锁超时时间 毫秒
     * @return
     */
    public void lock(String lockName, long lockTimeOut) {
        lockName = getLockName(lockName);
        while (true) {
            boolean result = redisTemplate.opsForValue().setIfAbsent(lockName, null, lockTimeOut, TimeUnit.MILLISECONDS);
            if (result) {
                return;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 尝试获取锁
     *
     * @param lockName    锁名称
     * @param lockTimeOut 锁超时时间 毫秒
     * @param waitTimeOut 获取锁等待超时时间 毫秒
     * @return
     */
    public boolean tryLock(String lockName, long lockTimeOut, long waitTimeOut) {
        lockName = getLockName(lockName);
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < waitTimeOut) {
            boolean result = redisTemplate.opsForValue().setIfAbsent(lockName, null, lockTimeOut, TimeUnit.MILLISECONDS);

            if (result) {
                return result;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 释放锁
     *
     * @param lockName 锁名称
     * @return
     */
    public boolean unlock(String lockName) {
        lockName = getLockName(lockName);
        return redisTemplate.delete(lockName);
    }

    private String getLockName(String lockName) {
        return String.format("%s%s", PREFIX, lockName);
    }
}