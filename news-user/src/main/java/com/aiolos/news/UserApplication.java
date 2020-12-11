package com.aiolos.news;

import com.rule.MyRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Aiolos
 * @date 2020/9/20 11:27 上午
 */
@SpringBootApplication
@MapperScan(basePackages = "com.aiolos.news.dao")
@ComponentScan(basePackages = "com.aiolos")    // 容器会扫描这个包下所有的@Component、@Configuration、@Bean、@Service等
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableEurekaClient
@EnableCircuitBreaker   // 开启Hystrix的熔断机制
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
