package com.wgf.mq;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 监听配置
 */
@Configuration
public class ListenerConfig extends CachingConfigurerSupport {

    /**
     * 消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        //订阅一个通道 该处的通道名是发布消息时的名称
        container.addMessageListener(new MessageListenerAdapter(new MyChannelListener()), new PatternTopic("myChannel"));
        return container;
    }

}
