package com.aiolos.news.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 构建消费端
 * @author Aiolos
 * @date 2020/12/20 3:57 上午
 */
@Component
@EnableBinding(MyStreamChannel.class)
public class MyStreamConsumer {

    /**
     * 监听并且实现消息的消费和相关业务的处理
     */
    @StreamListener(MyStreamChannel.INPUT)
    public void receiveMsg() {

    }
}