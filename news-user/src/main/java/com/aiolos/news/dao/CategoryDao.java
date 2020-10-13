package com.aiolos.news.dao;

import com.aiolos.news.pojo.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao extends BaseMapper<Category> {
}