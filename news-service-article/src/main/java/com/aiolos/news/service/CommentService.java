package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.PagedResult;

/**
 * @author Aiolos
 * @date 2021/6/5 2:25 下午
 */
public interface CommentService {

    /**
     * 保存评论信息
     * @param articleId 文章主键Id
     * @param fatherId  该条评论的父级评论
     * @param content   评论内容
     * @param userId    评论者
     * @param nickname  评论者昵称
     * @param userFace  评论者头像
     */
    void createComment(String articleId, String fatherId, String content, String userId, String nickname, String userFace) throws CustomizedException;

    /**
     * 分页查询文章评论列表
     * @param articleId 文章主键Id
     * @param page      第几页
     * @param pageSize  每页显示数
     * @return
     */
    PagedResult queryArticleComments(String articleId, Integer page, Integer pageSize);

    /**
     * 分页查询作家所有文章的评论列表
     * @param writerId  作家主键Id
     * @param page      第几页
     * @param pageSize  每页显示数
     * @return
     */
    PagedResult queryWriterCommentsMng(String writerId, Integer page, Integer pageSize);

    /**
     * 删除评论
     * @param writerId  作家主键Id
     * @param commentId 评论主键Id
     */
    void deleteComment(String writerId, String commentId) throws CustomizedException;
}
