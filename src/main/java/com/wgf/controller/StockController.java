package com.wgf.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: ken 😃
 * @create: 2022-02-09 16:58
 **/
@Slf4j
@Api(tags = "库存api")
@RestController
public class StockController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ExecutorService executorService;

    @ApiOperation(value = "不带锁的秒杀测试")
    @GetMapping("decr")
    public void decr() {
        Runnable task = () -> {
            int  num   = getNum();
            Long stock = redisTemplate.opsForValue().decrement("stock", num);

            if (stock < 0) {
                log.error("扣减库存失败，购买数量：{}", num);
                redisTemplate.opsForValue().increment("stock", num);
            } else {
                log.info("库存成功扣减");
            }
        };

        for (int i = 0; i < 1500; i++) {
            executorService.execute(task);
        }

        // 测试最总结果： 库存数量还有1的时候，多个线程下单数量为1下单失败，库存数量不能准确控制
    }

    @ApiOperation(value = "使用watch实现秒杀")
    @GetMapping("decr2")
    public void decr2() {
        Runnable task = () -> {
            Boolean flag = decrStock("stock", getNum(), 100);
            if (flag) {
                log.info("下单成功");
            } else {
                log.info("下单失败");
            }
        };

        for (int i = 0; i < 3000; i++) {
            executorService.execute(task);
        }
    }


    /**
     * 秒杀扣减库存实现
     *
     * @param key      库存key
     * @param num      购买数量
     * @param tryCount 重试扣减次数
     * @return
     */
    private Boolean decrStock(String key, int num, int tryCount) {
        Boolean result = (Boolean) this.redisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                for (int i = 0; i < tryCount; i++) {
                    operations.watch(key);
                    Integer stockNum = (Integer) operations.opsForValue().get(key);
                    if (stockNum < num) {
                        // log.warn("库存不足");
                        operations.unwatch();
                        try {
                            TimeUnit.MILLISECONDS.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    operations.multi();
                    operations.opsForValue().decrement(key, num);

                    List<Object> result = null;
                    result = operations.exec();

                    if (Objects.nonNull(result) && result.size() > 0) {
                        //log.info("剩余库存：{}", result.get(0));
                        return true;
                    }
                }
                return false;
            }
        });

        return result;
    }


    public static int getNum() {
        int min = 1;
        int max = 20;

        Random random = new Random();
        int    value  = random.nextInt(max + min) + min;

        return value;
    }
}
