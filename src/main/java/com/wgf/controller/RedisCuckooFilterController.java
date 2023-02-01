package com.wgf.controller;

import com.wgf.filter.cuckoo.RedisCuckooFilter;
import com.wgf.filter.registry.CuckooFilterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: Redis插件布谷鸟过滤器
 * @author: ken 😃
 * @create: 2022-02-22 18:57
 **/
@Api(tags = "Redis 插件 布谷鸟过滤器")
@RestController
@RequestMapping("cuckoo")
public class RedisCuckooFilterController {
    @Autowired
    private CuckooFilterRegistry cuckooFilterRegistry;

    private static final String filterName = "cuckoo-filter";


    @ApiOperation(value = "创建布谷鸟过滤器")
    @GetMapping("reserve")
    public boolean reserve() {
        RedisCuckooFilter cuckooFilter = cuckooFilterRegistry.getFilter(filterName);
        return cuckooFilter.reserve(10000, -1);
    }


    @ApiOperation(value = "添加数据到布谷鸟过滤器")
    @ApiImplicitParam(name = "item", value = "添加数据", dataType = "obj")
    @GetMapping("add")
    public boolean add(@RequestParam("item") Object item) {
        RedisCuckooFilter cuckooFilter = cuckooFilterRegistry.getFilter(filterName);
        return cuckooFilter.add(item);
    }


    @ApiOperation(value = "数据不存在，添加数据到布谷鸟过滤器")
    @ApiImplicitParam(name = "item", value = "添加数据", dataType = "obj")
    @GetMapping("add_nx")
    public boolean addNx(@RequestParam("item") Object item) {
        RedisCuckooFilter cuckooFilter = cuckooFilterRegistry.getFilter(filterName);
        return cuckooFilter.addNx(item);
    }


    @ApiOperation(value = "判断数据是否存在")
    @ApiImplicitParam(name = "item", value = "判断数据", dataType = "obj")
    @GetMapping("exists")
    public boolean exists(@RequestParam("item") Object item) {
        RedisCuckooFilter cuckooFilter = cuckooFilterRegistry.getFilter(filterName);
        return cuckooFilter.exists(item);
    }


    @ApiOperation(value = "删除数据")
    @ApiImplicitParam(name = "item", value = "判断数据", dataType = "obj")
    @GetMapping("del")
    public boolean del(@RequestParam("item") Object item) {
        RedisCuckooFilter cuckooFilter = cuckooFilterRegistry.getFilter(filterName);
        return cuckooFilter.del(item);
    }
}