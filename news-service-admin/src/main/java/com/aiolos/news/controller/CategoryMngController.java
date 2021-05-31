package com.aiolos.news.controller;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.controller.admin.CategoryMngControllerApi;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.SaveCategoryBO;
import com.aiolos.news.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/26 5:23 上午
 */
@Slf4j
@RestController
public class CategoryMngController extends BaseController implements CategoryMngControllerApi {

    public final CategoryService categoryService;

    public CategoryMngController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CommonResponse getCatList() {

        log.info("Enter the method getCats");

        // 先从redis中查询，如果有则返回，没有则从数据库中查询并保存到redis中
        String allCatsJson = redis.get(REDIS_ALL_CATEGORY);
        List<Category> categoryList = null;

        if (StringUtils.isBlank(allCatsJson)) {
            categoryList = categoryService.queryCategoryList();
            for (Category category : categoryList) {
                Integer id = category.getId();
                redis.set(REDIS_ALL_CATEGORY + ":" + id, JsonUtils.objectToJson(category), REDIS_ALL_CATEGORY_TIME_OUT);
            }
        } else {
            categoryList = JsonUtils.jsonToList(allCatsJson, Category.class);
        }
        return CommonResponse.ok(categoryList);
    }

    @Override
    public CommonResponse saveOrUpdateCategory(SaveCategoryBO saveCategoryBO) throws CustomizedException {
        categoryService.saveOrUpdateCategory(saveCategoryBO);
        return CommonResponse.ok();
    }
}
