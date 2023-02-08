package com.wgf.controller;

import io.swagger.annotations.Api;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ken 😃
 * @date: 2023-02-08
 * @description: Redis 緩存
 **/
@Api(tags = "redis缓存")
@RestController
@RequestMapping("cache")
public class CacheController {

    // key的生成逻辑：配置类中的前缀 + cacheNames + key(没有配置key就所有参数连接起来)
    // https://www.cnblogs.com/morganlin/p/12000223.html
    @Cacheable(cacheNames = "testCache#3600")
    @GetMapping("/get")
    public Object get(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put(name, name);
        return map;
    }
}
