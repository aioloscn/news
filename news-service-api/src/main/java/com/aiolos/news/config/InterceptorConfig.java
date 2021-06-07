package com.aiolos.news.config;

import com.aiolos.news.interceptors.*;
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

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor() {
        return new AdminTokenInterceptor();
    }

    @Bean
    public ArticleReadInterceptor articleReadInterceptor() {
        return new ArticleReadInterceptor();
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
                .addPathPatterns("/file/uploadFace")
                .addPathPatterns("/file/uploadSomeFiles")
                .addPathPatterns("/fans/follow")
                .addPathPatterns("/fans/unfollow")
                .addPathPatterns("/comment/mng")
                .addPathPatterns("/comment/delete");

        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/admin/adminIsExist")
                .addPathPatterns("/admin/addNewAdmin")
                .addPathPatterns("/admin/getAdminList")
                .addPathPatterns("/friendLinkMng/saveOrUpdateFriendLink")
                .addPathPatterns("/friendLinkMng/getFriendLinkList")
                .addPathPatterns("/friendLinkMng/delete")
                .addPathPatterns("/file/uploadToGridFS")
                .addPathPatterns("/file/readInGridFS");

        // 发表/修改/删除文章、发表/查看评论等等这些接口都是需要在用户激活以后才能进行
        registry.addInterceptor(userActiveInterceptor())
                .addPathPatterns("/file/uploadSomeFiles")
                .addPathPatterns("/fans/follow")
                .addPathPatterns("/fans/unfollow");

        // 阅读数防刷
        registry.addInterceptor(articleReadInterceptor())
                .addPathPatterns("/portal/readArticle");
    }
}
