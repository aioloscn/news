package com.aiolos.news.service.impl;

import com.aiolos.news.dao.CategoryDao;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/26 5:26 上午
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    public final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<Category> queryCategoryList() {

        return categoryDao.selectList(null);
    }
}
