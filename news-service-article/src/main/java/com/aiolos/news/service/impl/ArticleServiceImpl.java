package com.aiolos.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aiolos.news.common.enums.*;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.AliTextReviewUtils;
import com.aiolos.news.common.utils.DateUtils;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.config.RabbitMQConfig;
import com.aiolos.news.config.RabbitMQDelayQueueConfig;
import com.aiolos.news.controller.admin.CategoryMngControllerApi;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.dao.ArticleDao;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleAndCategoryBO;
import com.aiolos.news.pojo.bo.NewArticleBO;
import com.aiolos.news.pojo.bo.SaveCategoryBO;
import com.aiolos.news.pojo.eo.ArticleEO;
import com.aiolos.news.pojo.eo.DatingNewsEO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.ArticleService;
import com.aiolos.news.service.BaseService;
import com.aiolos.news.utils.ArticleUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Aiolos
 * @date 2020/11/26 5:34 下午
 */
@Slf4j
@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    private final ArticleDao articleDao;

    private final AliTextReviewUtils aliTextReviewUtils;

    private final ArticleUtil articleUtil;

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final CategoryMngControllerApi categoryMicroservice;

    private final UserControllerApi userMicroservice;

    public ArticleServiceImpl(ArticleDao articleDao, AliTextReviewUtils aliTextReviewUtils, ArticleUtil articleUtil,
                              ElasticsearchTemplate elasticsearchTemplate, CategoryMngControllerApi categoryMicroservice, UserControllerApi userMicroservice) {
        this.articleDao = articleDao;
        this.aliTextReviewUtils = aliTextReviewUtils;
        this.articleUtil = articleUtil;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.categoryMicroservice = categoryMicroservice;
        this.userMicroservice = userMicroservice;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void createArticle(NewArticleBO newArticleBO, Category category) throws CustomizedException {

        String articleId = idWorker.nextIdStr();

        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);
        article.setId(articleId);
        article.setCategoryId(category.getId());
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setIsDelete(YesOrNo.NO.getType());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        // 定时发布文章或者定时任务将爬虫数据上传，本身携带的发布时间
        if (article.getIsAppoint().equals(ArticleAppointType.TIMEING.getType()) || newArticleBO.getPublishTime() != null) {
            article.setPublishTime(newArticleBO.getPublishTime());
        } else {
            article.setPublishTime(new Date());
        }

        // 通过阿里智能AI实现对文章文本的自动检测
        String reviewTextResult = aliTextReviewUtils.reviewTextContent(newArticleBO.getContent());
        if (StringUtils.isBlank(reviewTextResult)) {
            // 修改标记为需要人工审核
            article.setArticleStatus(ArticleReviewStatus.WAITING_MANUAL.getType());
        } else if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.PASS.getType())) {
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
        // 机审失败直接打回，不用定时发布和保存ES数据了
        if (StringUtils.isNotBlank(reviewTextResult) &&!reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.BLOCK.getType()) &&
                article.getIsAppoint().equals(ArticleAppointType.TIMEING.getType())) {

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
            // 保存文章数据到ES
            rabbitTemplate.convertAndSend(RabbitMQDelayQueueConfig.EXCHANGE_DELAY, "delay.create.article",
                    articleId, messagePostProcessor);
        }

        // 如果机审成功且不需要人工复审，则直接生成静态页面
        if (StringUtils.isNotBlank(reviewTextResult) && reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.PASS.getType())) {
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
            if (article.getIsAppoint().equals(ArticleAppointType.IMMEDIATELY.getType())) {
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

        queryWrapper.orderByDesc("publish_time");

        IPage<Article> articlePage = new Page<>(pageNum, pageSize);
        articlePage = articleDao.selectPage(articlePage, queryWrapper);
        PagedResult pagedResult = setterPagedResult(articlePage);

        // 根据文章ids批量获取文章阅读量
        List<Article> articles = (List<Article>) pagedResult.getRecords();
        List<String> readCountIdList = new ArrayList<>();
        articles.forEach(a -> {
            // 构建文章id的阅读数list
            readCountIdList.add(REDIS_ARTICLE_READ_COUNTS + ":" + a.getId());
        });
        List<String> readCountsRedisList = redis.mget(readCountIdList);

        List<String> commentIdList = new ArrayList<>();
        articles.forEach(a -> {
            // 构建文章id的评论数list
            commentIdList.add(REDIS_ARTICLE_COMMENT_COUNTS + ":" + a.getId());
        });
        List<String> commentsRedisList = redis.mget(commentIdList);

        for (int i = 0; i < articles.size(); i++) {
            Integer readCounts = readCountsRedisList.get(i) == null ? 0 : Integer.valueOf(readCountsRedisList.get(i));
            articles.get(i).setReadCounts(readCounts);

            Integer commentCounts = commentsRedisList.get(i) == null ? 0 : Integer.valueOf(commentsRedisList.get(i));
            articles.get(i).setCommentCounts(commentCounts);
        }
        pagedResult.setRecords(articles);
        return pagedResult;
    }

    @Override
    public PagedResult queryAllList(Integer status, Integer page, Integer pageSize) {
        IPage<Article> articlePage = new Page<>(page, pageSize);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (status != null && status == 12)
            queryWrapper.and(wrapper -> wrapper.eq("article_status", ArticleReviewStatus.REVIEWING.getType())
                                        .or()
                                        .eq("article_status", ArticleReviewStatus.WAITING_MANUAL.getType()));
        else if (status != null && status.equals(ArticleReviewStatus.SUCCESS.getType()))
            queryWrapper.and(wrapper -> wrapper.eq("article_status", ArticleReviewStatus.SUCCESS.getType()));
        else if (status != null && status.equals(ArticleReviewStatus.FAILED.getType()))
            queryWrapper.and(wrapper -> wrapper.eq("article_status", ArticleReviewStatus.FAILED.getType()));
        else if (status != null && status.equals(ArticleReviewStatus.WITHDRAW.getType()))
            queryWrapper.and(wrapper -> wrapper.eq("article_status", ArticleReviewStatus.WITHDRAW.getType()));
        queryWrapper.eq("is_delete", YesOrNo.NO.getType());
        queryWrapper.orderByDesc("publish_time");
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

    @Override
    public void restoreEs() {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", YesOrNo.NO.getType());
        wrapper.eq("article_status", ArticleReviewStatus.SUCCESS.getType());
        wrapper.eq("is_appoint", ArticleAppointType.IMMEDIATELY.getType());
        List<Article> articleList = articleDao.selectList(wrapper);
        articleList.forEach(article -> {
            ArticleEO articleEO = new ArticleEO();
            BeanUtils.copyProperties(article, articleEO);
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(articleEO).build();
            String index = elasticsearchTemplate.index(indexQuery);
        });
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void publishNewsFromESData() {
        // 从ES获取数据，拿到最新的500条
        Pageable pageable = PageRequest.of(0, 100);
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).withPageable(pageable)
                .withSort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)).build();
        AggregatedPage<DatingNewsEO> datingNews = elasticsearchTemplate.queryForPage(query, DatingNewsEO.class);
        List<DatingNewsEO> content = datingNews.getContent();
        // 将有序的集合反转过来，最新发表的新闻最后插入，id越大
        if (content == null || content.isEmpty())
            return;

        List<DatingNewsEO> newList = new ArrayList<>(content);
        Collections.reverse(newList);

        NewArticleBO newArticleBO = new NewArticleBO();
        newArticleBO.setArticleCover(StringUtils.EMPTY);
        newArticleBO.setArticleType(ArticleCoverType.WORDS.getType());
        newArticleBO.setIsAppoint(ArticleAppointType.IMMEDIATELY.getType());

        // 获取发布者的Id，这里默认为网站作者
        UserBasicInfoVO user = userMicroservice.getUserByName("Aiolos");
        if (user != null)
            newArticleBO.setPublishUserId(user.getId());

        for (DatingNewsEO eo: newList) {
            // 保存新闻Id，每次执行进来判断下，已存在直接跳过这篇新闻
            String newId = eo.getId();
            boolean keyIsExist = redis.keyIsExist(ES_NEW_ID + ":" + newId);
            if (keyIsExist)
                continue;

            // 从redis查看文章类型是否存在，不存在则创建
            String categoryStr = eo.getPayload().getCategory().trim();
            log.info("categoryStr: {}", categoryStr);
            if (StringUtils.isBlank(categoryStr))
                return;

            Set<String> categorySet = redis.keys(REDIS_ALL_CATEGORY + ":*");
            Category categoryInRedis;
            // 用于保存到redis和传给rabbitmq
            Category category = new Category();
            boolean isExist = false;
            List<String> keyList = new ArrayList<>(categorySet);
            // 根据redis keys批量获取对应的数据集合
            List<String> categories = redis.mget(keyList);
            for (String s : categories) {
                categoryInRedis = JsonUtils.jsonToPojo(s, Category.class);
                if (StringUtils.equals(categoryStr, categoryInRedis.getName())) {
                    isExist = true;
                    newArticleBO.setCategoryId(categoryInRedis.getId());
                    BeanUtil.copyProperties(categoryInRedis, category);
                    break;
                }
            }
            if (!isExist) {
                SaveCategoryBO saveCategoryBO = new SaveCategoryBO();
                saveCategoryBO.setName(categoryStr);
                saveCategoryBO.setTagColor("#c93e07");
                try {
                    // 保存文章分类，返回主键，主键保存到redis，下次就不用保存了
                    CommonResponse resp = categoryMicroservice.saveOrUpdateCategory(saveCategoryBO);
                    if (resp != null && resp.getData() != null && resp.getCode() == HttpStatus.SC_OK) {
                        Integer id = (Integer) resp.getData();
                        newArticleBO.setCategoryId(id);
                        category = new Category();
                        category.setId(id);
                        category.setName(categoryStr);
                        category.setTagColor(saveCategoryBO.getTagColor());
                        redis.set(REDIS_ALL_CATEGORY + ":" + id, JsonUtils.objectToJson(category), REDIS_ALL_CATEGORY_TIME_OUT);
                    } else {
                        log.info("保存文章分类失败，saveCategoryBO: {}", JsonUtils.objectToJson(saveCategoryBO));
                    }
                } catch (CustomizedException e) {
                    log.error("保存爬虫数据时新增文章分类失败");
                    e.printStackTrace();
                }
            }

            newArticleBO.setTitle(eo.getPayload().getTitle());
            newArticleBO.setContent(eo.getPayload().getContent());
            newArticleBO.setPublishTime(DateUtils.strToDate(eo.getPayload().getPubtime()));

            // 组装完数据交给rabbitmq去上传新闻文章
            // NewArticleBO + Category
            NewArticleAndCategoryBO bo = new NewArticleAndCategoryBO();
            BeanUtil.copyProperties(newArticleBO, bo);
            bo.setNewId(eo.getId());
            bo.setId(category.getId());
            bo.setName(category.getName());
            bo.setTagColor(category.getTagColor());
            log.info("组装完数据交给rabbitmq去上传新闻文章，category: {}", JsonUtils.objectToJson(category));
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE, "article.insert", JsonUtils.objectToJson(bo));
        }
    }
}
