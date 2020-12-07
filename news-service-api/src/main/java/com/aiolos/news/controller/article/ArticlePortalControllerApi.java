package com.aiolos.news.controller.article;

import com.aiolos.news.common.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Aiolos
 * @date 2020/12/6 6:39 下午
 */
@Api(value = "门户端文章业务的controller", tags = "门户端文章业务的controller")
@RequestMapping("/portal")
public interface ArticlePortalControllerApi {

    @ApiOperation(value = "首页查询文章列表", notes = "首页查询文章列表", httpMethod = "GET")
    @GetMapping("/list")
    CommonResponse list(@RequestParam String keyword, @RequestParam Integer category,
                        @ApiParam(name = "page", value = "查询第几页", required = false)
                        @RequestParam Integer page,
                        @ApiParam(name = "pageSize", value = "每一页显示的条数", required = false)
                        @RequestParam Integer pageSize);
}
