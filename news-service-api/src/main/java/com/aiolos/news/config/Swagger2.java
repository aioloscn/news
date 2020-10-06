package com.aiolos.news.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Aiolos
 * @date 2020/9/27 11:02 上午
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    //    http://localhost:8003/swagger-ui.html     原路径
    //    http://localhost:8003/doc.html            新路径

    // 配置swagger2核心配置 docket
    @Bean
    public Docket createRestApi() {
//        Predicate<RequestHandler> adminPredicate = RequestHandlerSelectors.basePackage("com.aiolos.news.controller");
//        Predicate<RequestHandler> articlePredicate = RequestHandlerSelectors.basePackage("com.aiolos.news.controller");
//        Predicate<RequestHandler> userPredicate = RequestHandlerSelectors.basePackage("com.aiolos.news.controller");
//        Predicate<RequestHandler> filesPredicate = RequestHandlerSelectors.basePackage("com.aiolos.news.controller");
        Predicate<RequestHandler> predicate = RequestHandlerSelectors.basePackage("com.aiolos.news.controller");

        return new Docket(DocumentationType.SWAGGER_2)  // 指定api类型为swagger2
                .apiInfo(apiInfo())                 // 用于定义api文档汇总信息
                .select()
                .apis(predicate)
//                .apis(Predicates.or(userPredicate, adminPredicate, filesPredicate))
//                .apis(Predicates.or(adminPredicate, articlePredicate, userPredicate, filesPredicate))
                .paths(PathSelectors.any())         // 所有controller
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("新闻·自媒体接口api")                       // 文档页标题
                .contact(new Contact("aiolos",
                        "https://www.aiolosxhx.com",
                        "aiolosxhx@163.com"))                   // 联系人信息
                .description("专为新闻·自媒体平台提供的api文档")      // 详细信息
                .version("1.0.1")                               // 文档版本号
                .termsOfServiceUrl("https://www.aiolosxhx.com")     // 网站地址
                .build();
    }
}
