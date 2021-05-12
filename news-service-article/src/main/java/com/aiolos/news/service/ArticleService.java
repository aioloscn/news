package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleBO;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/11/26 5:33 下午
 */
public interface ArticleService {

    void createArticle(NewArticleBO newArticleBO, Category category) throws CustomizeException;

    void updateAppointToPublish() throws CustomizeException;

    PagedResult queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize);
}
