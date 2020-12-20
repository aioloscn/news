package com.aiolos.news.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ的配置类
 * @author Aiolos
 * @date 2020/12/21 3:18 上午
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 定义交换机的名字
     */
    public static final String EXCHANGE_ARTICLE = "exchange_article";

    /**
     * 定义队列的名字
     */
    public static final String QUEUE_DOWNLOAD_HTML = "queue_download_html";

    /**
     * 创建交换机
     * @return
     */
    @Bean(EXCHANGE_ARTICLE)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_ARTICLE).build();
    }

    /**
     * 创建队列
     */
    @Bean(QUEUE_DOWNLOAD_HTML)
    public Queue queue() {
        return new Queue(QUEUE_DOWNLOAD_HTML);
    }

    /**
     * 队列绑定交换机
     */
    @Bean
    public Binding binding(@Qualifier(EXCHANGE_ARTICLE) Exchange exchange, @Qualifier(QUEUE_DOWNLOAD_HTML) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("article.*").noargs();
    }
}