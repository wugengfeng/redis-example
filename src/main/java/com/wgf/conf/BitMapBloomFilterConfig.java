package com.wgf.conf;

import com.wgf.filter.bitmap.BitMapBloomFilterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @description: RedisBitMap布隆过滤器配置
 * @author: ken 😃
 * @create: 2022-02-19 15:40
 **/
@Configuration
public class BitMapBloomFilterConfig {
    @Bean
    public BitMapBloomFilterRegistry bitMapBloomFilterRegistry(RedisTemplate redisTemplate) {
        return new BitMapBloomFilterRegistry(redisTemplate);
    }
}
