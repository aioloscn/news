package com.aiolos.news.service.impl;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.CustomizedBeanUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.dao.CommentsDao;
import com.aiolos.news.pojo.Comments;
import com.aiolos.news.pojo.vo.ArticleDetailVO;
import com.aiolos.news.pojo.vo.CommentSubVO;
import com.aiolos.news.pojo.vo.CommentsVO;
import com.aiolos.news.service.ArticlePortalService;
import com.aiolos.news.service.BaseService;
import com.aiolos.news.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
import java.util.Date;
import java.util.List;

/**
 * @author Aiolos
 * @date 2021/6/5 2:25 下午
 */
@Slf4j
@Service
public class CommentServiceImpl extends BaseService implements CommentService {

    private final CommentsDao commentsDao;
    private final ArticlePortalService articlePortalService;

    public CommentServiceImpl(CommentsDao commentsDao, ArticlePortalService articlePortalService) {
        this.commentsDao = commentsDao;
        this.articlePortalService = articlePortalService;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void createComment(String articleId, String fatherId, String replyId, String content, String userId, String nickname, String userFace) throws CustomizedException {
        ArticleDetailVO article = articlePortalService.queryDetailContainsRegularlyPublishedArticles(articleId);
        Comments comments = new Comments();
        comments.setId(idWorker.nextIdStr());
        comments.setWriterId(article.getPublishUserId());
        comments.setFatherId(fatherId);
        comments.setReplyId(replyId);
        comments.setArticleId(articleId);
        comments.setArticleTitle(article.getTitle());
        comments.setArticleCover(article.getArticleCover());
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickname);
        comments.setCommentUserFace(userFace);
        comments.setContent(content);
        comments.setCreateTime(new Date());
        int result = commentsDao.insert(comments);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.COMMENT_FAILED);
            }
        }
    }

    @Override
    public PagedResult queryArticleComments(String articleId, Integer page, Integer pageSize) {
        IPage<Comments> commentsPage = new Page<>(page, pageSize);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("article_id", articleId);
        queryWrapper.eq("father_id", 0);
        queryWrapper.orderByAsc("create_time");
        commentsPage = commentsDao.selectPage(commentsPage, queryWrapper);
        // 重组评论列表，将楼中楼回复塞到父级评论list里，如果上级被删除则子集全部不显示
        PagedResult pagedResult = setterPagedResult(commentsPage);
        List<Comment> records = (List<Comment>) pagedResult.getRecords();
        List<CommentsVO> commentsVOs = CustomizedBeanUtils.copyListProperties(records, CommentsVO::new);
        for (CommentsVO c : commentsVOs) {
            List<CommentSubVO> subComments = commentsDao.querySubCommentList(c.getId());
            c.setCommentSubVOs(subComments);
        }
        pagedResult.setRecords(commentsVOs);
        return pagedResult;
    }

    @Override
    public PagedResult queryWriterCommentsMng(String writerId, Integer page, Integer pageSize) {
        IPage<Comments> commentsIPage = new Page<>(page, pageSize);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("writer_id", writerId);
        commentsIPage = commentsDao.selectPage(commentsIPage, queryWrapper);
        return setterPagedResult(commentsIPage);
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void deleteComment(String writerId, String commentId) throws CustomizedException {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("id", commentId);
        wrapper.eq("writer_id", writerId);
        int result = commentsDao.delete(wrapper);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.FAILED_TO_DELETE_COMMENT);
            }
        }
    }
}
