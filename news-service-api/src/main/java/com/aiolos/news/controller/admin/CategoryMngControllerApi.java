package com.aiolos.news.controller.admin;

import com.aiolos.news.common.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Aiolos
 * @date 2020/11/26 5:15 上午
 */
@Api(value = "文章分类维护", tags = {"文章分类维护controller"})
@RequestMapping("/categoryMng")
public interface CategoryMngControllerApi {

    @ApiOperation(value = "用户端查询分类列表", notes = "用户端查询分类列表", httpMethod = "GET")
    @GetMapping("/getCats")
    CommonResponse getCats();
}