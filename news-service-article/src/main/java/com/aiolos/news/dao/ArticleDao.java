package com.aiolos.news.dao;

import com.aiolos.news.pojo.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDao extends BaseMapper<Article> {

    /**
     * 更新定时发布为即时发布
     * @return
     */
    int updateAppointToPublish();
}