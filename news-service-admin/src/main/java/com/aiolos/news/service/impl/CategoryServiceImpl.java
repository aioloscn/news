package com.aiolos.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.dao.CategoryDao;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.SaveCategoryBO;
import com.aiolos.news.service.BaseService;
import com.aiolos.news.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/26 5:26 上午
 */
@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    public final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<Category> queryCategoryList() {
        return categoryDao.selectList(null);
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizeException.class)
    @Override
    public void saveOrUpdateCategory(SaveCategoryBO saveCategoryBO) throws CustomizeException {

        redis.del(REDIS_ALL_CATEGORY);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("name", saveCategoryBO.getName());
        Category category = categoryDao.selectOne(queryWrapper);
        int result = 0;
        if (category != null) {
            if (!category.getTagColor().equals(saveCategoryBO.getTagColor())) {
                category.setTagColor(saveCategoryBO.getTagColor());
                result = categoryDao.updateById(category);
            }
        } else {
            category = new Category();
            BeanUtil.copyProperties(saveCategoryBO, category);
            result = categoryDao.insert(category);
        }
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizeException(ErrorEnum.SAVE_CATEGORY_FAILED);
            }
        }
        redis.del(REDIS_ALL_CATEGORY);
    }
}
