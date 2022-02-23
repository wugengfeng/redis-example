package com.wgf.controller;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @description: 单机版本布隆过滤器
 * @author: ken 😃
 * @create: 2022-02-17 17:30
 **/

@Api(tags = "单机版本布隆过滤器")
@Slf4j
@RestController
@RequestMapping("guava")
public class GuavaBloomFilterController {

    /**
     * 插入多少数据
     */
    private static final int insertions = 1000000;

    /**
     * 期望的误判率
     */
    private static double fpp = 0.02;

    /**
     * 单机版本布隆过滤器
     */
    private BloomFilter<String> bf;

    /**
     * 用于校验bloomFilter 准确性
     */
    private Set<String> keySet = new HashSet<>();

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 测试时放开注解
     * 数据预热
     * 初始化布隆过滤器
     */
    //@PostConstruct
    public void init() {
        bf = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), insertions, fpp);

        // 添加缓存数据
        for (int i = 0; i < 5000; i++) {
            String key   = generate("k", i);
            String value = generate("v", i);

            // 数据添加布隆过滤器
            bf.put(key);
            keySet.add(key);
            redisTemplate.opsForValue().set(key, value);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始检索点 取值[0, 5000]", dataType = "int"),
            @ApiImplicitParam(name = "end", value = "截止检索点 取值[0, 5000] 需要比start大", dataType = "int")
    })
    @ApiOperation(value = "测试单机版布隆过滤器")
    @GetMapping("test")
    public Map<String, Object> test(@RequestParam("start") Integer start, @RequestParam("end") Integer end) {

        // 根据参数生成数据
        List<String> dataList = IntStream.range(start, end)
                .mapToObj(line -> generate("k", line))
                .collect(Collectors.toList());

        // 布隆过滤器拦截值是否存在
        int          count = 0;
        List<String> keys  = new ArrayList<>();
        for (String data : dataList) {

            // 布隆过滤器判断数据是否存在
            if (bf.mightContain(data)) {
                keys.add(data);
            }

            // 判断布隆过滤器准确性
            if (keySet.contains(data)) {
                count++;
            }
        }

        double accuracy = keys.size() / count * 100.0d;

        List                result = redisTemplate.opsForValue().multiGet(keys);
        Map<String, Object> map    = new HashMap<>(8);
        map.put("bloomFilter 准确率", accuracy);
        map.put("缓存数据命中个数", result.size());

        return map;
    }

    private String generate(Object obj1, Object obj2) {
        return String.format("%s_%s", obj1, obj2);
    }

}
