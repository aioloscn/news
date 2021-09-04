package com.aiolos.news.controller;

import com.aiolos.news.common.enums.*;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.article.ArticleControllerApi;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleBO;
import com.aiolos.news.pojo.eo.ArticleEO;
import com.aiolos.news.service.ArticleService;
import com.aiolos.news.utils.ArticleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
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

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ArticleController(ArticleService articleService, ArticleUtil articleUtil, ElasticsearchTemplate elasticsearchTemplate) {
        this.articleService = articleService;
        this.articleUtil = articleUtil;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public CommonResponse createArticle(@Valid NewArticleBO newArticleBO) throws CustomizedException {

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
            return CommonResponse.error(ErrorEnum.TOKEN_INVALID);
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
    public CommonResponse doReview(String articleId, Integer passOrNot) throws CustomizedException {
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

            if (StringUtils.isBlank(articleMongoId) || articleMongoId.equalsIgnoreCase("null")) {
                articleService.updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.getType());
                log.info(ErrorEnum.GRIDFS_HAS_NO_RESOURCES_ARTICLE_REVIEW_FAILED.getErrMsg());
                return CommonResponse.error(ErrorEnum.GRIDFS_HAS_NO_RESOURCES_ARTICLE_REVIEW_FAILED);
            } else {
                // 存储到对应的文章，进行关联保存
                articleService.updateArticleToGridFS(articleId, articleMongoId);
                // 发送消息到mq队列，让消费者监听并且执行下载html
                articleUtil.downloadArticleHtmlByMQ(articleId, articleMongoId);
                // 审核通过，查询article把响应的字段信息存入ES
                Article article = articleService.queryById(articleId);
                if (article.getIsAppoint().equals(ArticleAppointType.IMMEDIATELY.getType())) {
                    // 如果是即时发布的文章则直接存入ES，如果是定时发布的文章则在延迟队列消费端去执行
                    ArticleEO articleEO = new ArticleEO();
                    BeanUtils.copyProperties(article, articleEO);
                    IndexQuery indexQuery = new IndexQueryBuilder().withObject(articleEO).build();
                    String index = elasticsearchTemplate.index(indexQuery);
                    log.info("审核文章{}，保存ES索引: {}", articleId, index);
                    if (StringUtils.isBlank(index)) {
                        log.error("审核文章{}，保存ES索引失败", articleId);
                    }
                }
            }
        }
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse withdraw(String userId, String articleId) throws CustomizedException {
        // 查询文章获取articleMongoId
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return CommonResponse.error(ErrorEnum.UNDO_FAILED_THE_ARTICLE_DOES_NOT_EXIST);
        }
        String articleMongoId = article.getMongoFileId();
        if (StringUtils.isBlank(articleMongoId)) {
            log.warn("文章{}撤回，不存在MongoDB数据");
            return CommonResponse.ok();
        }
        articleService.withdraw(userId, articleId);
        // 删除GridFS存储的关联数据
//        articleUtil.deleteFromGridFS(articleMongoId);
        // 删除对应的静态html
//        articleUtil.deleteArticleHtmlByMQ(articleId);
        // 删除ES中的文章数据
//        elasticsearchTemplate.delete(ArticleEO.class, articleId);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse delete(String userId, String articleId) throws CustomizedException {
        // 查询文章获取articleMongoId
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return CommonResponse.error(ErrorEnum.UNDO_FAILED_THE_ARTICLE_DOES_NOT_EXIST);
        }
        String articleMongoId = article.getMongoFileId();
        if (StringUtils.isBlank(articleMongoId)) {
            log.warn("文章{}删除，不存在MongoDB数据");
        }
        articleService.delete(userId, articleId);
        // 删除GridFS存储的关联数据
        articleUtil.deleteFromGridFS(articleMongoId);
        // 删除对应的静态html
        articleUtil.deleteArticleHtmlByMQ(articleId);
        // 删除ES中的文章数据
        elasticsearchTemplate.delete(ArticleEO.class, articleId);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse restoreEs() {
        articleService.restoreEs();
        return CommonResponse.ok();
    }
}
