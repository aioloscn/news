package com.aiolos.news.controller.admin;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.config.MyServiceList;
import com.aiolos.news.controller.article.fallbacks.CategoryMngControllerFallbackFactory;
import com.aiolos.news.pojo.bo.SaveCategoryBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2020/11/26 5:15 上午
 */
@Api(value = "文章分类维护", tags = {"文章分类维护controller"})
@RequestMapping("/categoryMng")
@FeignClient(value = MyServiceList.NEWS_SERVICE_CATEGORY, fallbackFactory = CategoryMngControllerFallbackFactory.class)
public interface CategoryMngControllerApi {

    @ApiOperation(value = "用户端查询分类列表", notes = "用户端查询分类列表", httpMethod = "GET")
    @GetMapping("/getCatList")
    CommonResponse getCatList();

    @ApiOperation(value = "新增或更新分类", httpMethod = "POST")
    @PostMapping("/saveOrUpdateCategory")
    CommonResponse saveOrUpdateCategory(@Valid @RequestBody SaveCategoryBO saveCategoryBO) throws CustomizedException;
}