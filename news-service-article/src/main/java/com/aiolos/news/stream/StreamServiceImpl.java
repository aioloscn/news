package com.aiolos.news.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Service;

/**
 * 开启绑定器，绑定通道channel
 * @author Aiolos
 * @date 2020/12/20 3:44 上午
 */
@Service
@EnableBinding(MyStreamChannel.class)
public class StreamServiceImpl implements StreamService {

    private final MyStreamChannel myStreamChannel;

    public StreamServiceImpl(MyStreamChannel myStreamChannel) {
        this.myStreamChannel = myStreamChannel;
    }

    @Override
    public void sendMsg() {

    }
}
