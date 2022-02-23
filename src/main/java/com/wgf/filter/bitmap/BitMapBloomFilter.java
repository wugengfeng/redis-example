package com.wgf.filter.bitmap;

import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Redis BitMap 实现的布隆过滤器
 *
 * @param <T>
 */
public class BitMapBloomFilter<T> {
    /**
     * 布隆过滤器Key
     */
    private String key;

    /**
     * 需要执行hash函数次数
     */
    private int numHashFunctions;

    /**
     * 过滤器大小
     */
    private int bitSize;

    private Funnel<T>     funnel;
    private RedisTemplate redisTemplate;

    /**
     * @param redisTemplate
     * @param expectedInsertions 预计插入的元素数量
     */
    public BitMapBloomFilter(RedisTemplate redisTemplate, String key, int expectedInsertions) {
        this.key           = key;
        this.redisTemplate = redisTemplate;
        this.funnel        = (Funnel<T>) Funnels.stringFunnel(Charset.defaultCharset());
        bitSize            = optimalNumOfBits(expectedInsertions, 0.02);
        numHashFunctions   = optimalNumOfHashFunctions(expectedInsertions, bitSize);
    }

    /**
     * @param redisTemplate
     * @param expectedInsertions 预计插入的数量
     * @param fpp                期望的误判率
     */
    public BitMapBloomFilter(RedisTemplate redisTemplate, String key, int expectedInsertions, double fpp) {
        this.key           = key;
        this.redisTemplate = redisTemplate;
        this.funnel        = (Funnel<T>) Funnels.stringFunnel(Charset.defaultCharset());
        bitSize            = optimalNumOfBits(expectedInsertions, fpp);
        numHashFunctions   = optimalNumOfHashFunctions(expectedInsertions, bitSize);
    }

    /**
     * @param redisTemplate
     * @param funnel
     * @param expectedInsertions 预计插入的数量
     * @param fpp                期望的误判率
     */
    public BitMapBloomFilter(RedisTemplate redisTemplate, String key, Funnel<T> funnel, int expectedInsertions, double fpp) {
        this.key           = key;
        this.redisTemplate = redisTemplate;
        this.funnel        = funnel;
        bitSize            = optimalNumOfBits(expectedInsertions, fpp);
        numHashFunctions   = optimalNumOfHashFunctions(expectedInsertions, bitSize);
    }

    /**
     * 使用murmur hash算法计算值的下标
     *
     * @param value
     * @return
     */
    public int[] murmurHashOffset(T value) {
        int[] offset = new int[numHashFunctions];

        long hash64 = Hashing.murmur3_128().hashObject(value, funnel).asLong();
        int  hash1  = (int) hash64;
        int  hash2  = (int) (hash64 >>> 32);
        for (int i = 1; i <= numHashFunctions; i++) {
            int nextHash = hash1 + i * hash2;
            if (nextHash < 0) {
                nextHash = ~nextHash;
            }
            offset[i - 1] = nextHash % bitSize;
        }

        return offset;
    }

    /**
     * 计算bit数组长度
     */
    private int optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算hash方法执行次数
     */
    private int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    /**
     * 删除布隆过滤器
     *
     * @param key KEY
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 根据给定的布隆过滤器添加值，在添加一个元素的时候使用，批量添加的性能差
     *
     * @param bloomFilterHelper 布隆过滤器对象
     * @param key               KEY
     * @param value             值
     * @param <T>               泛型，可以传入任何类型的value
     */
    public void add(T value) {
        int[] offset = this.murmurHashOffset(value);
        for (int i : offset) {
            redisTemplate.opsForValue().setBit(key, i, true);
        }
    }

    /**
     * 根据给定的布隆过滤器添加值，在添加一批元素的时候使用，批量添加的性能好，使用pipeline方式(如果是集群下，请使用优化后RedisPipeline的操作)
     *
     * @param bloomFilterHelper 布隆过滤器对象
     * @param key               KEY
     * @param valueList         值，列表
     * @param <T>               泛型，可以传入任何类型的value
     */
    public void addList(List<? extends T> valueList) {
        redisTemplate.executePipelined(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.openPipeline();
                for (T value : valueList) {
                    int[] offset = murmurHashOffset(value);
                    for (int i : offset) {
                        connection.setBit(key.getBytes(), i, true);
                    }
                }
                return null;
            }
        });
    }

    /**
     * 根据给定的布隆过滤器判断值是否存在
     *
     * @param bloomFilterHelper 布隆过滤器对象
     * @param key               KEY
     * @param value             值
     * @return 是否存在
     */
    public boolean contains(T value) {
        int[] offset = this.murmurHashOffset(value);
        for (int i : offset) {
            if (!redisTemplate.opsForValue().getBit(key, i)) {
                return false;
            }
        }
        return true;
    }
}
