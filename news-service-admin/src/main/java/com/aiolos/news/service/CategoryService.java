package com.aiolos.news.service;

import com.aiolos.news.pojo.Category;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/26 5:25 上午
 */
public interface CategoryService {

    List<Category> queryCategoryList();
}
