package com.aiolos.news.controller;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.controller.article.CommentControllerApi;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.pojo.bo.CommentReplyBO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2021/6/5 11:42 上午
 */
@Slf4j
@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    private final CommentService commentService;
    private final UserControllerApi userMicroservice;

    public CommentController(CommentService commentService, UserControllerApi userMicroservice) {
        this.commentService = commentService;
        this.userMicroservice = userMicroservice;
    }

    @Override
    public CommonResponse createComment(@Valid CommentReplyBO commentReplyBO) throws CustomizedException {
        // 1. 根据留言用户的id查询用户昵称，用于保存到数据库进行字段的冗余处理，从而避免多表关联查询的性能影响
        String userId = commentReplyBO.getCommentUserId();
        String nickname = StringUtils.EMPTY;
        String userFace = StringUtils.EMPTY;
        CommonResponse userResp = userMicroservice.getUserBasicInfo(userId);
        if (userResp != null && userResp.getCode().equals(HttpStatus.OK.value())) {
            String userJson = JsonUtils.objectToJson(userResp.getData());
            UserBasicInfoVO userBasicInfoVO = JsonUtils.jsonToPojo(userJson, UserBasicInfoVO.class);
            nickname = userBasicInfoVO.getNickname();
            userFace = userBasicInfoVO.getFace();
        }

        // 2. 保存评论信息到数据库
        commentService.createComment(commentReplyBO.getArticleId(), commentReplyBO.getFatherId(), commentReplyBO.getContent(), userId, nickname, userFace);
        // TODO 评论保存到ES中

        // 评论数累加
        redis.increment(REDIS_ARTICLE_COMMENT_COUNTS + ":" + commentReplyBO.getArticleId(), 1);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse counts(String articleId) {
        Integer counts = getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId);
        return CommonResponse.ok(counts);
    }

    @Override
    public CommonResponse list(String articleId, Integer page, Integer pageSize) {
        if (page == null) page = START_PAGE;
        if (pageSize == null) pageSize = PAGE_SIZE;
        return CommonResponse.ok(commentService.queryArticleComments(articleId, page, pageSize));
    }

    @Override
    public CommonResponse mng(String writerId, Integer page, Integer pageSize) {
        if (page == null) page = START_PAGE;
        if (pageSize == null) pageSize = PAGE_SIZE;
        return CommonResponse.ok(commentService.queryWriterCommentsMng(writerId, page, pageSize));
    }

    @Override
    public CommonResponse delete(String writerId, String articleId, String commentId) throws CustomizedException {
        commentService.deleteComment(writerId, commentId);
        redis.decrement(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId, 1);
        return CommonResponse.ok();
    }
}
