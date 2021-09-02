package com.aiolos.news.controller.article;

import com.aiolos.news.config.MyServiceList;
import com.aiolos.news.controller.article.fallbacks.ArticleHtmlControllerFallbackFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Aiolos
 * @date 2021/5/18 3:40 上午
 */
@Api(value = "静态化文章业务的controller", tags = "静态化文章业务的controller")
@RequestMapping("/article/html")
@FeignClient(value = MyServiceList.NEWS_SERVICE_ARTICLE_HTML, fallbackFactory = ArticleHtmlControllerFallbackFactory.class)
public interface ArticleHtmlControllerApi {

    @ApiOperation(value = "从GridFS下载静态文章资源到前端项目", httpMethod = "GET")
    @GetMapping("/download")
    Integer download(@RequestParam String articleId, @RequestParam String articleMongoId);

    @ApiOperation(value = "删除前端项目中的指定的静态文章", httpMethod = "GET")
    @GetMapping("/delete")
    Integer delete(@RequestParam String articleId);
}
