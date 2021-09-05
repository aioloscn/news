package com.aiolos.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.dao.CategoryDao;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.SaveCategoryBO;
import com.aiolos.news.service.BaseService;
import com.aiolos.news.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
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

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void saveOrUpdateCategory(SaveCategoryBO saveCategoryBO) throws CustomizedException {

        redis.del(REDIS_ALL_CATEGORY);
        Category category = new Category();
        BeanUtil.copyProperties(saveCategoryBO, category);
        int result;
        if (category.getId() == null) {
            // 不存在重复名则新增
            if (!queryCatIsExist(category.getName(), null)) {
                result = categoryDao.insert(category);
            } else {
                throw new CustomizedException(ErrorEnum.ARTICLE_CATEGORY_ALREADY_EXISTS);
            }
        } else {
            // 查询修改的分类名称是否已存在
            if (!queryCatIsExist(category.getName(), saveCategoryBO.getOldName())) {
                // 根据主键修改名称和颜色
                result = categoryDao.updateById(category);
            } else {
                throw new CustomizedException(ErrorEnum.ARTICLE_CATEGORY_ALREADY_EXISTS);
            }
        }

        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.SAVE_CATEGORY_FAILED);
            }
        }
        redis.del(REDIS_ALL_CATEGORY);
    }

    private boolean queryCatIsExist(String name, String oldName) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        if (StringUtils.isNotBlank(oldName))
            wrapper.ne("name", oldName);
        List<Category> categoryList = categoryDao.selectList(wrapper);
        return categoryList != null && !categoryList.isEmpty() && categoryList.size() > 0;
    }
}
