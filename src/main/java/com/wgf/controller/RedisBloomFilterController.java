package com.wgf.controller;

import com.wgf.filter.bloom.RedisBloomFilter;
import com.wgf.filter.registry.BloomFilterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: Redis插件布隆过滤器
 * @author: ken 😃
 * @create: 2022-02-22 17:23
 **/
@Api(tags = "Redis 插件 布隆过滤器")
@RestController
@RequestMapping("bloom")
public class RedisBloomFilterController {
    @Autowired
    private BloomFilterRegistry bloomFilterRegistry;

    private static final String filterName = "bloom-filter";


    @ApiOperation(value = "创建布隆过滤器")
    @GetMapping("reserve")
    public boolean reserve() {
        RedisBloomFilter redisBloomFilter = bloomFilterRegistry.getFilter(filterName);
        return redisBloomFilter.reserve(0.001, 10000, -1);
    }


    @ApiOperation(value = "添加数据到布隆过滤器")
    @ApiImplicitParam(name = "item", value = "添加数据", dataType = "obj")
    @GetMapping("add")
    public boolean add(@RequestParam("item") Object item) {
        RedisBloomFilter redisBloomFilter = bloomFilterRegistry.getFilter(filterName);
        return redisBloomFilter.add(item);
    }


    @ApiOperation(value = "批量添加数据到布隆过滤器")
    @ApiImplicitParam(name = "values", value = "添加数据，json数组 [1,2,3]")
    @PostMapping("multi_add")
    public List<Object> multiAdd(@RequestBody List<Object> values) {
        RedisBloomFilter redisBloomFilter = bloomFilterRegistry.getFilter(filterName);
        return redisBloomFilter.multiAdd(values);
    }


    @ApiOperation(value = "判断数据是否存在")
    @ApiImplicitParam(name = "item", value = "判断数据", dataType = "obj")
    @GetMapping("exists")
    public boolean exists(@RequestParam("item") Object item) {
        RedisBloomFilter redisBloomFilter = bloomFilterRegistry.getFilter(filterName);
        return redisBloomFilter.exists("test");
    }


    @ApiOperation(value = "批量判断数据是否存在")
    @ApiImplicitParam(name = "values", value = "判断数据,json数组 [1,2,3]")
    @PostMapping("multi_exists")
    public List<Object> multiExists(@RequestBody List<Object> values) {
        RedisBloomFilter redisBloomFilter = bloomFilterRegistry.getFilter(filterName);
        return redisBloomFilter.multiExists(values);
    }
}
