package com.wgf.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 消息监听器
 */
@Slf4j
public class MyChannelListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("channel：{}，监听消息：{}", new String(message.getChannel()), message.toString());
    }
}
