package com.aiolos.news.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 解决跨域问题
 * @author Aiolos
 * @date 2020/9/30 8:42 下午
 */
@Configuration
public class CorsConfig {

    public CorsConfig() {}

    @Bean
    public CorsFilter corsFilter() {

        // 1.添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        // 设置是否允许发送cookie
        config.setAllowCredentials(true);
        // 设置允许请求的方式
        config.addAllowedMethod("*");
        // 设置允许的Header
        config.addAllowedHeader("*");
        // 2.为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);
        // 3.返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }
}
