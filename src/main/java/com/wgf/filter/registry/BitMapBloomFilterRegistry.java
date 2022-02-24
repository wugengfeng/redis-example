package com.wgf.filter.registry;

import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.wgf.filter.bitmap.BitMapBloomFilter;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 布隆过滤器注册表
 * @author: ken 😃
 * @create: 2022-02-19 14:48
 **/
public class BitMapBloomFilterRegistry {

    public BitMapBloomFilterRegistry(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 默认需要插入的元素
     */
    private static final int DEFAULT_EXPECTED_INSERTIONS = 2000;

    private RedisTemplate redisTemplate;

    /**
     * 过滤器前缀
     */
    private final String prefix = "redis:bloomfilter:";

    /**
     * 布隆过滤器容器
     */
    private final Map<String, BitMapBloomFilter<CharSequence>> filters = new ConcurrentHashMap<>(16);


    /**
     * 获取布隆过滤器
     * 如果获取不到则根据默认配置创建布隆过滤器
     *
     * @param filterName
     * @return
     */

    public BitMapBloomFilter<CharSequence> obtain(String filterName) {
        filterName = this.getFilterName(filterName);
        BitMapBloomFilter<CharSequence> bitMapBloomFilter = filters.computeIfAbsent(filterName,
                key -> new BitMapBloomFilter<CharSequence>(redisTemplate, key, DEFAULT_EXPECTED_INSERTIONS));
        return bitMapBloomFilter;
    }

    /**
     * 获取布隆过滤器，获取不到则根据配置创建
     *
     * @param filterName         布隆过滤器名称
     * @param expectedInsertions 预计插入的数量
     * @param fpp                期望的误判率
     * @return
     */
    public BitMapBloomFilter<CharSequence> obtain(String filterName, int expectedInsertions, double fpp) {
        filterName = this.getFilterName(filterName);
        Funnel<CharSequence>            funnel            = (Funnel<CharSequence>) Funnels.stringFunnel(Charset.defaultCharset());
        BitMapBloomFilter<CharSequence> bitMapBloomFilter = filters.computeIfAbsent(filterName,
                key -> new BitMapBloomFilter<CharSequence>(redisTemplate, key, funnel, expectedInsertions, fpp));
        return bitMapBloomFilter;
    }

    private String getFilterName(String name) {
        return String.format("%s%s", prefix, name);
    }
}
