package com.redis.notify.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.listener.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Classname RedisTimer
 * @Description
 * @Date 2020/5/19 10:12
 * @Author yangzhou
 */
@Component
public class RedisTimer extends KeyspaceEventMessageListener implements ApplicationEventPublisherAware {
    private static final Logger logger = LoggerFactory.getLogger(RedisTimer.class);
    // 对db6的以test为前缀的key设置过期监听,参数依次为，db，事件类型, key
    // 这里使用事件通知，而不是命名空间通知 PUBLISH __keyspace@0__:mykey del,注意参数顺序
    private static final Topic KEYEVENT_EXPIRED_TOPIC = new PatternTopic("__keyevent@6__:expired");
    // 对所有库的key设置过期监听
//    private static final Topic KEYEVENT_EXPIRED_TOPIC = new PatternTopic("__keyevent@*__:expired");
    @Nullable
    private ApplicationEventPublisher publisher;

    public RedisTimer(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
        listenerContainer.addMessageListener(this, KEYEVENT_EXPIRED_TOPIC);
    }

    protected void doHandleMessage(Message message) {
        this.publishEvent(new RedisKeyExpiredEvent(message.getBody()));
    }

    protected void publishEvent(RedisKeyExpiredEvent event) {
        if (this.publisher != null) {
            this.publisher.publishEvent(event);
        }

    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        //过期的key
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        logger.info("redis key 过期：pattern={},channel={},key={}", new String(pattern), channel, key);
    }
}

