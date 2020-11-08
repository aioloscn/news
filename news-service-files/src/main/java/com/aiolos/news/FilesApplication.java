package com.aiolos.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Aiolos
 * @date 2020/11/6 2:56 上午
 */
@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = "com.aiolos")
public class FilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilesApplication.class, args);
    }
}
