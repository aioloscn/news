package com.aiolos.news.controller.article;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.config.MyServiceList;
import com.aiolos.news.controller.article.fallbacks.ArticlePortalControllerFallbackFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Aiolos
 * @date 2020/12/6 6:39 下午
 */
@Api(value = "门户端文章业务的controller", tags = "门户端文章业务的controller")
@RequestMapping("/portal")
//@FeignClient(value = MyServiceList.NEWS_SERVICE_ARTICLE, fallbackFactory = ArticlePortalControllerFallbackFactory.class)
public interface ArticlePortalControllerApi {

    @ApiOperation(value = "首页查询文章列表", notes = "首页查询文章列表", httpMethod = "GET")
    @GetMapping("/list")
    CommonResponse list(@RequestParam(required = false) String keyword, @RequestParam(required = false) Integer category,
                        @ApiParam(name = "page", value = "查询第几页", required = false)
                        @RequestParam Integer page,
                        @ApiParam(name = "pageSize", value = "每一页显示的条数", required = false)
                        @RequestParam Integer pageSize);

    @ApiOperation(value = "首页查询最新热闻列表", notes = "首页查询最新热闻", httpMethod = "GET")
    @GetMapping("/hotList")
    CommonResponse hotList();

    @ApiOperation(value = "文章详情查询", notes = "文章详情查询", httpMethod = "GET")
    @GetMapping("/detail")
    CommonResponse detail(@RequestParam String articleId);

    @ApiOperation(value = "文章详情查询，包含定时发布的文章", notes = "文章详情查询，多用于生成静态文章html", httpMethod = "GET")
    @GetMapping("/detailContainsRegularlyPublishedArticles")
    CommonResponse detailContainsRegularlyPublishedArticles(@RequestParam String articleId);

    @ApiOperation(value = "获得文章阅读数", httpMethod = "GET")
    @GetMapping("/readCounts")
    Integer readCounts(@RequestParam String articleId);

    @ApiOperation(value = "阅读文章，文章阅读量增加", notes = "阅读文章，文章阅读量增加", httpMethod = "POST")
    @PostMapping("/readArticle")
    CommonResponse readArticle(@RequestParam String articleId, HttpServletRequest request);

    @ApiOperation(value = "分页查询作家文章", httpMethod = "GET")
    @GetMapping("/queryArticleListOfWriter")
    CommonResponse queryArticleListOfWriter(@ApiParam(name = "writerId", value = "作家Id", required = true) @RequestParam String writerId,
                                            @ApiParam(name = "page", value = "查询第几页", required = false) @RequestParam(required = false) Integer page,
                                            @ApiParam(name = "pageSize", value = "每一页显示的条数", required = false) @RequestParam(required = false) Integer pageSize);

    @ApiOperation(value = "查询作家近期佳文", httpMethod = "GET")
    @GetMapping("/queryGoodArticleListOfWriter")
    CommonResponse queryGoodArticleListOfWriter(@ApiParam(name = "writerId", value = "作家Id", required = true) @RequestParam String writerId);
}
