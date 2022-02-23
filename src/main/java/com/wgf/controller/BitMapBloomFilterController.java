package com.wgf.controller;

import com.wgf.filter.bitmap.BitMapBloomFilter;
import com.wgf.filter.bitmap.BitMapBloomFilterRegistry;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Api(tags = "Redis BitMap 版布隆过滤器")
@Slf4j
@RestController
@RequestMapping("bitmap")
public class BitMapBloomFilterController {

    @Autowired
    private BitMapBloomFilterRegistry bitMapBloomFilterRegistry;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 数据预热
     */
    //@PostConstruct
    public void init() {
        BitMapBloomFilter<CharSequence> bloomFilter = bitMapBloomFilterRegistry.obtain("test");
        List<String> valueList = IntStream.range(1, 2000)
                .mapToObj(line -> String.format("%s_%s", "k", line))
                .collect(Collectors.toList());

        // 初始化布隆过滤器
        bloomFilter.addList(valueList);

        // 缓存数据初始化
        for (String s : valueList) {
            redisTemplate.opsForValue().set(s, s);
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始检索点 取值[1, 2000]", dataType = "int"),
            @ApiImplicitParam(name = "end", value = "截止检索点 取值[1, 2000] 需要比start大", dataType = "int")
    })
    @ApiOperation(value = "Redis BitMap版布隆过滤器")
    @GetMapping("test")
    public Map<String, Object> test(@RequestParam("start") Integer start, @RequestParam("end") Integer end) {
        // 获取布隆过滤器
        BitMapBloomFilter<CharSequence> bloomFilter = bitMapBloomFilterRegistry.obtain("test");

        // 根据参数生成数据
        List<String> dataList = IntStream.range(start, end)
                .mapToObj(line -> String.format("%s_%s", "k", line))
                .collect(Collectors.toList());

        int          errNum        = 0;
        List<Object> cacheDataList = new ArrayList<>();
        for (String data : dataList) {
            boolean result = bloomFilter.contains(data);

            // 根据BloomFilter 过滤器判断，如果数据存在，则取缓存
            if (result) {
                Object cache = redisTemplate.opsForValue().get(data);
                cacheDataList.add(cache);
            } else {
                // 错误统计
                errNum++;
            }
        }

        Map<String, Object> map = new HashMap(8);
        map.put("命中缓存数据个数", cacheDataList.size());
        map.put("误判个数", errNum);

        return map;
    }
}
