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
 * @author: ken ð
 * @create: 2022-02-09 16:58
 **/
@Slf4j
@Api(tags = "åºå­api")
@RestController
public class StockController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ExecutorService executorService;

    @ApiOperation(value = "ä¸å¸¦éçç§ææµè¯")
    @GetMapping("decr")
    public void decr() {
        Runnable task = () -> {
            int  num   = getNum();
            Long stock = redisTemplate.opsForValue().decrement("stock", num);

            if (stock < 0) {
                log.error("æ£ååºå­å¤±è´¥ï¼è´­ä¹°æ°éï¼{}", num);
                redisTemplate.opsForValue().increment("stock", num);
            } else {
                log.info("åºå­æåæ£å");
            }
        };

        for (int i = 0; i < 1500; i++) {
            executorService.execute(task);
        }

        // æµè¯ææ»ç»æï¼ åºå­æ°éè¿æ1çæ¶åï¼å¤ä¸ªçº¿ç¨ä¸åæ°éä¸º1ä¸åå¤±è´¥ï¼åºå­æ°éä¸è½åç¡®æ§å¶
    }

    @ApiOperation(value = "ä½¿ç¨watchå®ç°ç§æ")
    @GetMapping("decr2")
    public void decr2() {
        Runnable task = () -> {
            Boolean flag = decrStock("stock", getNum(), 100);
            if (flag) {
                log.info("ä¸åæå");
            } else {
                log.info("ä¸åå¤±è´¥");
            }
        };

        for (int i = 0; i < 3000; i++) {
            executorService.execute(task);
        }
    }


    /**
     * ç§ææ£ååºå­å®ç°
     *
     * @param key      åºå­key
     * @param num      è´­ä¹°æ°é
     * @param tryCount éè¯æ£åæ¬¡æ°
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
                        // log.warn("åºå­ä¸è¶³");
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
                        //log.info("å©ä½åºå­ï¼{}", result.get(0));
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
