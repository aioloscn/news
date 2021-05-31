package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.SaveCategoryBO;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/26 5:25 上午
 */
public interface CategoryService {

    /**
     * 查询所有分类
     * @return
     */
    List<Category> queryCategoryList();

    /**
     * 保存或更新分类
     * @param saveCategoryBO
     */
    void saveOrUpdateCategory(SaveCategoryBO saveCategoryBO) throws CustomizedException;
}
