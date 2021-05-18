package com.aiolos.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Aiolos
 * @date 2021/5/17 8:55 下午
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.aiolos")
@EnableEurekaClient
@EnableHystrix
public class ArticleHtmlApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleHtmlApplication.class, args);
    }
}
