package com.aiolos.news.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Aiolos
 * @date 2020/12/9 2:46 下午
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableEurekaServer
public class EurekaClusterApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaClusterApplication.class, args);
    }
}
