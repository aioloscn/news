package com.aiolos.news.service.impl;

import com.aiolos.news.common.config.IdGeneratorSnowflake;
import com.aiolos.news.common.enums.*;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.AliTextReviewUtils;
import com.aiolos.news.common.utils.DateUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.config.RabbitMQDelayQueueConfig;
import com.aiolos.news.dao.ArticleDao;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleBO;
import com.aiolos.news.pojo.eo.ArticleEO;
import com.aiolos.news.service.ArticleService;
import com.aiolos.news.service.BaseService;
import com.aiolos.news.utils.ArticleUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/26 5:34 下午
 */
@Slf4j
@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    private final ArticleDao articleDao;

    private final IdGeneratorSnowflake snowflake;

    private final AliTextReviewUtils aliTextReviewUtils;

    private final ArticleUtil articleUtil;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ArticleServiceImpl(ArticleDao articleDao, IdGeneratorSnowflake snowflake,
                              AliTextReviewUtils aliTextReviewUtils, ArticleUtil articleUtil, ElasticsearchTemplate elasticsearchTemplate) {
        this.articleDao = articleDao;
        this.snowflake = snowflake;
        this.aliTextReviewUtils = aliTextReviewUtils;
        this.articleUtil = articleUtil;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void createArticle(NewArticleBO newArticleBO, Category category) throws CustomizedException {

        String articleId = snowflake.nextIdStr();

        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);
        article.setId(articleId);
        article.setCategoryId(category.getId());
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setIsDelete(YesOrNo.NO.getType());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        if (article.getIsAppoint().equals(ArticleAppointType.TIMEING.getType())) {
            article.setPublishTime(newArticleBO.getPublishTime());
        } else {
            article.setPublishTime(new Date());
        }

        // 通过阿里智能AI实现对文章文本的自动检测
        String reviewTextResult = aliTextReviewUtils.reviewTextContent(newArticleBO.getContent());
        if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.PASS.getType())) {
            // 修改标记为审核通过
            article.setArticleStatus(ArticleReviewStatus.SUCCESS.getType());
        } else if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.REVIEW.getType())) {
            // 修改标记为需要人工审核
            article.setArticleStatus(ArticleReviewStatus.WAITING_MANUAL.getType());
        } else if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.BLOCK.getType())) {
            // 修改标记为审核未通过
            article.setArticleStatus(ArticleReviewStatus.FAILED.getType());
        } else {
            article.setArticleStatus(ArticleReviewStatus.REVIEWING.getType());
        }

        int count = articleDao.insert(article);
        if (count != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.ARTICLE_CREATE_FAILED);
            }
        }

        // 发送延迟消息到mq，计算定时发布时间和当前时间的时间差，为往后延时的时间
        if (article.getIsAppoint().equals(ArticleAppointType.TIMEING.getType())) {

            Date publishTime = newArticleBO.getPublishTime();
            Date currentTime = new Date();
            int delayTime = (int) (publishTime.getTime() - currentTime.getTime());

            MessagePostProcessor messagePostProcessor = message -> {
                // 设置持久消息
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                // 设置消息的延迟时间，单位为ms
                message.getMessageProperties().setDelay(delayTime);
                return message;
            };

            log.info("exchange: {}, routingKey: {}, message: {}, delayTime: {}",
                    RabbitMQDelayQueueConfig.EXCHANGE_DELAY, "delay.create.article", articleId, DateUtils.fromDeadline(publishTime));
            // 下载静态html，保存文章数据到ES
            rabbitTemplate.convertAndSend(RabbitMQDelayQueueConfig.EXCHANGE_DELAY, "delay.create.article",
                    articleId, messagePostProcessor);
        }

        // 如果机审成功且不需要人工复审，则直接生成静态页面
        if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.PASS.getType())) {
            // 审核成功，生成文章静态html
            String articleMongoId = articleUtil.createArticleHtmlToGridFS(articleId);
            if (StringUtils.isBlank(articleMongoId) || articleMongoId.equalsIgnoreCase("null")) {
                // 静态文章html上传到GridFS出错，走人工审核
                log.error("静态文章html: {}上传到GridFS出错，走人工审核", articleId);
                this.updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.getType());
                return;
            }

            // 存储到对应的文章，进行关联保存
            this.updateArticleToGridFS(articleId, articleMongoId);
            // 发送消息到mq队列，让消费者监听并且执行下载html
            articleUtil.downloadArticleHtmlByMQ(articleId, articleMongoId);

            // 即时发布的文章直接保存文章数据到ES
            ArticleEO articleEO = new ArticleEO();
            BeanUtils.copyProperties(article, articleEO);
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(articleEO).build();
            String index = elasticsearchTemplate.index(indexQuery);
            log.info("创建文章{}，保存ES索引: {}", articleId, index);
            if (StringUtils.isBlank(index)) {
                log.error("创建文章{}，保存ES索引失败", articleId);
            }
        }
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void updateAppointToPublish() throws CustomizedException {
        int result = articleDao.updateAppointToPublish();
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            throw new CustomizedException(ErrorEnum.FAILED_TO_PUBLISH_AN_ARTICLE_ON_A_SCHEDULED_TASK);
        }
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void updateArticleToPublish(String articleId) throws CustomizedException {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.getType());
        article.setUpdateTime(new Date());
        int result = articleDao.updateById(article);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.FAILED_TO_POST_AN_ARTICLE_LATE);
            }
        }
    }

    @Override
    public PagedResult queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publish_user_id", userId);

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            queryWrapper.eq("article_status", status);
        }

        if (status != null && status == 12) {
            queryWrapper.and(wrapper -> wrapper.eq("article_status", ArticleReviewStatus.REVIEWING.getType())
                                        .or()
                                        .eq("article_status", ArticleReviewStatus.WAITING_MANUAL.getType()));
        }

        // 已删除的文章虽然仍旧保存在数据库，但在前端不显示
        queryWrapper.eq("is_delete", YesOrNo.NO.getType());

        if (startDate != null) {
            queryWrapper.ge("publish_time", startDate);
        }
        if (endDate != null) {
            queryWrapper.le("publish_time", endDate);
        }

        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("title", "%" + keyword + "%");
        }

        queryWrapper.orderByDesc("create_time");

        IPage<Article> articlePage = new Page<>(pageNum, pageSize);
        articlePage = articleDao.selectPage(articlePage, queryWrapper);
        PagedResult pagedResult = setterPagedResult(articlePage);

        // 根据文章ids批量获取文章阅读量
        List<Article> articles = (List<Article>) pagedResult.getRecords();
        List<String> idList = new ArrayList<>();
        articles.forEach(a -> {
            // 构建文章id的list
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + a.getId());
        });
        List<String> readCountsRedisList = redis.mget(idList);
        for (int i = 0; i < articles.size(); i++) {
            articles.get(i).setReadCounts(Integer.valueOf(readCountsRedisList.get(i)));
        }
        pagedResult.setRecords(articles);
        return pagedResult;
    }

    @Override
    public PagedResult queryAllList(Integer status, Integer page, Integer pageSize) {
        IPage<Article> articlePage = new Page<>(page, pageSize);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (status != null && status == 12) {
            queryWrapper.and(wrapper -> wrapper.eq("article_status", ArticleReviewStatus.REVIEWING.getType())
                                        .or()
                                        .eq("article_status", ArticleReviewStatus.WAITING_MANUAL.getType()));
        }
        queryWrapper.eq("is_delete", YesOrNo.NO.getType());
        queryWrapper.orderByDesc("create_time");
        articlePage = articleDao.selectPage(articlePage, queryWrapper);
        PagedResult pagedResult = setterPagedResult(articlePage);
        return pagedResult;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) throws CustomizedException {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(pendingStatus);
        article.setUpdateTime(new Date());
        int result = articleDao.updateById(article);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.UPDATE_ARTICLE_STATUS_FAILED);
            }
        }
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void withdraw(String userId, String articleId) throws CustomizedException {
        Article article = new Article();
        article.setId(articleId);
        article.setPublishUserId(userId);
        article.setArticleStatus(ArticleReviewStatus.WITHDRAW.getType());
        article.setMongoFileId(StringUtils.EMPTY);
        article.setUpdateTime(new Date());
        int result = articleDao.updateById(article);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.UPDATE_ARTICLE_STATUS_FAILED);
            }
        }
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void delete(String userId, String articleId) throws CustomizedException {
        Article article = new Article();
        article.setId(articleId);
        article.setPublishUserId(userId);
        article.setIsDelete(YesOrNo.YES.getType());
        article.setMongoFileId(StringUtils.EMPTY);
        article.setUpdateTime(new Date());
        int result = articleDao.updateById(article);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.UPDATE_ARTICLE_STATUS_FAILED);
            }
        }
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void updateArticleToGridFS(String articleId, String articleMongoId) throws CustomizedException {
        Article article = new Article();
        article.setId(articleId);
        article.setMongoFileId(articleMongoId);
        article.setUpdateTime(new Date());
        int result = articleDao.updateById(article);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.UPDATE_ARTICLE_STATUS_FAILED);
            }
        }
    }

    @Override
    public Article queryById(String articleId) {
        return articleDao.selectById(articleId);
    }
}
