package com.aiolos.news.dao;

import com.aiolos.news.pojo.Category;
import com.aiolos.news.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao extends MyMapper<Category> {
}