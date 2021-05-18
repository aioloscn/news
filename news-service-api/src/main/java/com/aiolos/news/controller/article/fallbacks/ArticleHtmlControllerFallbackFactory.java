package com.aiolos.news.controller.article.fallbacks;

import com.aiolos.news.controller.article.ArticleHtmlControllerApi;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Aiolos
 * @date 2021/5/18 5:20 上午
 */
@Slf4j
@Component
public class ArticleHtmlControllerFallbackFactory implements FallbackFactory<ArticleHtmlControllerApi> {
    @Override
    public ArticleHtmlControllerApi create(Throwable throwable) {
        return new ArticleHtmlControllerApi() {
            @Override
            public Integer download(String articleId, String articleMongoId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return HttpStatus.OK.value();
            }

            @Override
            public Integer delete(String articleId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return HttpStatus.OK.value();
            }
        };
    }
}
