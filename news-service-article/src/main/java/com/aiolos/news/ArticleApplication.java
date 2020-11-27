package com.aiolos.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Aiolos
 * @date 2020/11/25 5:57 上午
 */
@SpringBootApplication
@MapperScan(basePackages = "com.aiolos.news.dao")
@ComponentScan(basePackages = "com.aiolos")
public class ArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class, args);
    }
}
