package com.aiolos.news.config;

import com.aiolos.news.interceptors.PassportInterceptor;
import com.aiolos.news.interceptors.UserActiveInterceptor;
import com.aiolos.news.interceptors.UserTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Aiolos
 * @date 2020/10/7 8:13 下午
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    public UserActiveInterceptor userActiveInterceptor() {
        return new UserActiveInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册拦截器，配置哪些接口需要对应的拦截器拦截
        // 同一个手机号60s内只能执行一次获取短信验证码接口
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode");
        // 登录后才能获取完整账号信息和修改账号信息
        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/user/updateAccountInfo")
                .addPathPatterns("/file/uploadFace");
        // 发表/修改/删除文章、发表/查看评论等等这些接口都是需要在用户激活以后才能进行
//        registry.addInterceptor(userActiveInterceptor())
//                .addPathPatterns("");
    }
}
