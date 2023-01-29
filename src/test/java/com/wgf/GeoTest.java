package com.wgf;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * Geo 经纬度测试
 *
 * @author: ken 😃
 * @date: 2023-01-29
 * @description:
 **/
@Slf4j
@SpringBootTest
public class GeoTest {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "city";

    @Test
    public void geoAddTest() {
        redisTemplate.opsForGeo().add(KEY, new Point(114.100d, 22.200d), "xianggang");
        redisTemplate.opsForGeo().add(KEY, new Point(113.233, 23.166d), "guangzhou");
        redisTemplate.opsForGeo().add(KEY, new Point(113.516d, 22.300d), "zhuhai");
        redisTemplate.opsForGeo().add(KEY, new Point(114.066d, 22.616d), "shenzhen");
    }


    @Test
    public void geoPosTest() {
        List<Point> shenzhen = redisTemplate.opsForGeo().position(KEY, "shenzhen");
        log.info("x: {}, y: {}", shenzhen.get(0).getX(), shenzhen.get(0).getY());
    }


    @Test
    public void geoDistTest() {
        Distance distance = redisTemplate.opsForGeo().distance(KEY, "shenzhen", "guangzhou", Metrics.KILOMETERS);
        log.info("value: {}", distance.getValue());
    }


    @Test
    public void geoRadiusTest() {
        Point point = new Point(114.100d, 22.200d);
        Metrics kilometers = Metrics.KILOMETERS;
        Distance distance = new Distance(150, kilometers);
        Circle circle = new Circle(point, distance);

        GeoResults<RedisGeoCommands.GeoLocation> radius = redisTemplate.opsForGeo().radius(KEY, circle);
        radius.forEach(geo -> {
            log.info("name: {}", geo.getContent().getName());
        });
    }


    @Test
    public void geoRadiusByMemberTest() {
        GeoResults<RedisGeoCommands.GeoLocation> results = redisTemplate.opsForGeo().radius(KEY, "shenzhen", new Distance(100, Metrics.KILOMETERS));
        results.forEach(geo -> {
            log.info("name: {}", geo.getContent().getName());
        });
    }
}
