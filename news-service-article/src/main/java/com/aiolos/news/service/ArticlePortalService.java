package com.aiolos.news.service;

import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.vo.ArticleDetailVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/12/7 8:27 上午
 */
public interface ArticlePortalService {

    /**
     * 查询首页文章列表
     * @param keyword   关键字
     * @param category  类别
     * @param page      第几页
     * @param pageSize  每页显示的条数
     * @return
     */
    PagedResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize);

    /**
     * 从ES中查询首页文章列表
     * @param keyword   关键字
     * @param category  类别
     * @param page      第几页
     * @param pageSize  每页显示的条数
     * @return
     */
    PagedResult queryIndexArticleESList(String keyword, Integer category, Integer page, Integer pageSize);

    /**
     * 查询热门文章
     * @return
     */
    List<Article> queryHotList();

    /**
     * 从zset中获取阅读数最高的5篇文章Id，根据Id从ES中获取文章列表
     * @return
     */
    List<Article> queryESHotListByScore();

    /**
     * 根据文章主键查询文章详情
     * @param articleId
     * @return
     */
    ArticleDetailVO queryDetail(String articleId);

    /**
     * 文章详情查询，包含定时发布的文章
     * @param articleId
     * @return
     */
    ArticleDetailVO queryDetailContainsRegularlyPublishedArticles(String articleId);
}
