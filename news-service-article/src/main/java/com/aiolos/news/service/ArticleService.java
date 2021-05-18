package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleBO;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/11/26 5:33 下午
 */
public interface ArticleService {

    /**
     * 发布文章
     * @param newArticleBO
     * @param category
     * @throws CustomizeException
     */
    void createArticle(NewArticleBO newArticleBO, Category category) throws CustomizeException;

    /**
     * 发布定时文章
     * @throws CustomizeException
     */
    void updateAppointToPublish() throws CustomizeException;

    /**
     * 根据条件查询文章列表
     * @param userId
     * @param keyword
     * @param status
     * @param startDate
     * @param endDate
     * @param pageNum
     * @param pageSize
     * @return
     */
    PagedResult queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize);

    /**
     * 管理员查询所有文章
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult queryAllList(Integer status, Integer page, Integer pageSize);

    /**
     * 修改文章审核状态
     * @param articleId
     * @param pendingStatus
     * @throws CustomizeException
     */
    void updateArticleStatus(String articleId, Integer pendingStatus) throws CustomizeException;

    /**
     * 文章撤回
     * @param userId
     * @param articleId
     */
    void withdraw(String userId, String articleId) throws CustomizeException;

    /**
     * 逻辑删除文章
     * @param userId
     * @param articleId
     */
    void delete(String userId, String articleId) throws CustomizeException;

    /**
     * 关联文章和GridFS的html文件id
     * @param articleId
     * @param articleMongoId
     */
    void updateArticleToGridFS(String articleId, String articleMongoId) throws CustomizeException;

    /**
     * 根据主键查询文章信息
     * @param articleId
     * @return
     */
    Article queryById(String articleId);
}
