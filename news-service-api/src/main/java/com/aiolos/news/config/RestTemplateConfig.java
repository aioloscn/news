package com.aiolos.news.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Aiolos
 * @date 2020/12/7 7:34 下午
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced   // 默认的负载均衡算法：轮询
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
