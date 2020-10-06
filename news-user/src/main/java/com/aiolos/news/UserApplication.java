package com.aiolos.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Aiolos
 * @date 2020/9/20 11:27 上午
 */
@SpringBootApplication
@MapperScan(basePackages = "com.aiolos.news.dao")
@ComponentScan("com.aiolos")    // 容器会扫描这个包下所有的@Component、@Configuration、@Bean、@Service等
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
