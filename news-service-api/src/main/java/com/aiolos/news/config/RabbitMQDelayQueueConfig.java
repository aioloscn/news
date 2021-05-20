package com.aiolos.news.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延时队列配置类
 * @author Aiolos
 * @date 2021/5/20 6:07 下午
 */
@Configuration
public class RabbitMQDelayQueueConfig {

    /**
     * 定义交换机的名字
     */
    public static final String EXCHANGE_DELAY = "exchange_delay";

    /**
     * 定义队列的名字
     */
    public static final String DELAY_QUEUE = "delay_queue";

    /**
     * 创建交换机
     * @return
     */
    @Bean(EXCHANGE_DELAY)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_DELAY)
                .delayed()          // 开启支持延迟消息
                .durable(true)
                .build();
    }

    @Bean(DELAY_QUEUE)
    public Queue delayQueue() {
        return new Queue(DELAY_QUEUE);
    }

    @Bean
    public Binding bindingDelayQueue(@Qualifier(EXCHANGE_DELAY) Exchange exchange, @Qualifier(DELAY_QUEUE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("delay.create.#").noargs();
    }
}
