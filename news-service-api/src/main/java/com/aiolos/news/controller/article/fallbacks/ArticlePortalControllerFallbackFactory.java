package com.aiolos.news.controller.article.fallbacks;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.controller.article.ArticlePortalControllerApi;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Aiolos
 * @date 2021/5/15 5:29 下午
 */
@Slf4j
@Component
public class ArticlePortalControllerFallbackFactory implements FallbackFactory<ArticlePortalControllerApi> {

    @Override
    public ArticlePortalControllerApi create(Throwable throwable) {
        return new ArticlePortalControllerApi() {
            @Override
            public CommonResponse list(String keyword, Integer category, Integer page, Integer pageSize) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse hotList() {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse detail(String articleId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse detailContainsRegularlyPublishedArticles(String articleId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public Integer readCounts(String articleId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return 0;
            }

            @Override
            public CommonResponse readArticle(String articleId, HttpServletRequest request) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse queryGoodArticleListOfWriter(String writerId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }
        };
    }
}
