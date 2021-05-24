package com.aiolos.news.service;

import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.vo.ArticleDetailVO;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/12/7 8:27 上午
 */
public interface ArticlePortalService {

    /**
     * 首页查询文章列表
     * @param keyword   关键字
     * @param category  类别
     * @param page      第几页
     * @param pageSize  每页显示的条数
     * @return
     */
    PagedResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize);

    List<Article> queryHotList();

    ArticleDetailVO queryDetail(String articleId);

    ArticleDetailVO queryDetailContainsRegularlyPublishedArticles(String articleId);
}
