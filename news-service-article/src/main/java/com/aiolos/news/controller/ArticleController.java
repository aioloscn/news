package com.aiolos.news.controller;

import com.aiolos.news.common.enums.ArticleReviewStatus;
import com.aiolos.news.common.enums.YesOrNo;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ArticleCoverType;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.article.ArticleControllerApi;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleBO;
import com.aiolos.news.service.ArticleService;
import com.aiolos.news.utils.ArticleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @author Aiolos
 * @date 2020/11/26 6:27 上午
 */
@Slf4j
@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    private final ArticleService articleService;

    private final ArticleUtil articleUtil;

    public ArticleController(ArticleService articleService, ArticleUtil articleUtil) {
        this.articleService = articleService;
        this.articleUtil = articleUtil;
    }

    @Override
    public CommonResponse createArticle(@Valid NewArticleBO newArticleBO) throws CustomizeException {

        // 判断文章封面类型，单图必填，纯文字则设置为空
        if (newArticleBO.getArticleType().equals(ArticleCoverType.ONE_IMAGE.getType())) {
            if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                return CommonResponse.error(ErrorEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        } else if (newArticleBO.getArticleType().equals(ArticleCoverType.WORDS.getType())) {
            newArticleBO.setArticleCover("");
        }

        Category category = null;

        // 判断redis中是否保存里分类ID
        Set<String> categoryKeys = redis.keys(REDIS_ALL_CATEGORY + "*");
        if (categoryKeys == null) {
            return CommonResponse.error(ErrorEnum.SYSTEM_OPERATION_ERROR);
        } else {
            List<String> keyList = new ArrayList<>(categoryKeys);
            List<String> categories = redis.mget(keyList);
            for (String s : categories) {
                Category c = JsonUtils.jsonToPojo(s, Category.class);
                if (c.getId().equals(newArticleBO.getCategoryId())) {
                    category = c;
                    break;
                }
            }
            if (category == null) {
                return CommonResponse.error(ErrorEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
        }

        articleService.createArticle(newArticleBO, category);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return CommonResponse.error(ErrorEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        if (pageNum == null) {
            pageNum = START_PAGE;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult pagedResult = articleService.queryMyArticleList(userId, keyword, status, startDate, endDate, pageNum, pageSize);
        return CommonResponse.ok(pagedResult);
    }

    @Override
    public CommonResponse queryAllList(Integer status, Integer page, Integer pageSize) {
        if (page == null) page = START_PAGE;
        if (pageSize == null) pageSize = PAGE_SIZE;
        return CommonResponse.ok(articleService.queryAllList(status, page, pageSize));
    }

    @Override
    public CommonResponse doReview(String articleId, Integer passOrNot) throws CustomizeException {
        Integer pendingStatus;
        if (passOrNot.equals(YesOrNo.YES.getType())) {
            pendingStatus = ArticleReviewStatus.SUCCESS.getType();
        } else if (passOrNot.equals(YesOrNo.NO.getType())) {
            pendingStatus = ArticleReviewStatus.FAILED.getType();
        } else {
            return CommonResponse.error(ErrorEnum.ARTICLE_REVIEW_ERROR);
        }
        articleService.updateArticleStatus(articleId, pendingStatus);
        if (pendingStatus.equals(ArticleReviewStatus.SUCCESS.getType())) {
            // 审核成功，生成文章静态html
            String articleMongoId = articleUtil.createArticleHtmlToGridFS(articleId);
            // 存储到对应的文章，进行关联保存
            articleService.updateArticleToGridFS(articleId, articleMongoId);
            // 发送消息到mq队列，让消费者监听并且执行下载html
            articleUtil.downloadArticleHtmlByMQ(articleId, articleMongoId);
        }
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse withdraw(String userId, String articleId) throws CustomizeException {
        // 查询文章获取articleMongoId
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return CommonResponse.error(ErrorEnum.UNDO_FAILED_THE_ARTICLE_DOES_NOT_EXIST);
        }
        String articleMongoId = article.getMongoFileId();
        articleService.withdraw(userId, articleId);
        // 删除GridFS存储的关联数据
        articleUtil.deleteFromGridFS(articleMongoId);
        // 删除对应的静态html
        articleUtil.deleteArticleHtmlByMQ(articleId);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse delete(String userId, String articleId) throws CustomizeException {
        // 查询文章获取articleMongoId
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return CommonResponse.error(ErrorEnum.UNDO_FAILED_THE_ARTICLE_DOES_NOT_EXIST);
        }
        String articleMongoId = article.getMongoFileId();
        articleService.delete(userId, articleId);
        // 删除GridFS存储的关联数据
        articleUtil.deleteFromGridFS(articleMongoId);
        // 删除对应的静态html
        articleUtil.deleteArticleHtmlByMQ(articleId);
        return CommonResponse.ok();
    }
}
