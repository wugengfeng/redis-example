package com.wgf.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 线程池配置
 * @author: ken 😃
 * @create: 2022-02-16 10:53
 **/
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(100);
    }
}
