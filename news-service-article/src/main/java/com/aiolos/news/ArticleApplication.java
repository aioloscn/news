package com.aiolos.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Aiolos
 * @date 2020/11/25 5:57 上午
 */
@SpringBootApplication
@MapperScan(basePackages = "com.aiolos.news.dao")
@ComponentScan(basePackages = "com.aiolos")
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.aiolos")    // 服务调用者
@EnableHystrix      // 服务调用者断路器开启，服务提供者已经写了全局服务熔断降级方法，加上这个注解才可用
public class ArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class, args);
    }
}
