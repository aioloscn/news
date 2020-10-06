package com.aiolos.news.dao;

import com.aiolos.news.pojo.Article;
import com.aiolos.news.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDao extends MyMapper<Article> {
}