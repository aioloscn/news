package com.aiolos.news.controller;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.IPUtils;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.article.ArticlePortalControllerApi;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.pojo.vo.ArticleDetailVO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.ArticlePortalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Aiolos
 * @date 2020/12/6 9:12 下午
 */
@Slf4j
@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {

    private final ArticlePortalService articlePortalService;

    private final RestTemplate restTemplate;

    private final UserControllerApi userMicroservice;

    private final StringRedisTemplate redisTemplate;

    public ArticlePortalController(ArticlePortalService articlePortalService, RestTemplate restTemplate, UserControllerApi userMicroservice, StringRedisTemplate redisTemplate) {
        this.articlePortalService = articlePortalService;
        this.restTemplate = restTemplate;
        this.userMicroservice = userMicroservice;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CommonResponse list(String keyword, Integer category, Integer page, Integer pageSize) {
        if (page == null) {
            page = START_PAGE;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult pagedResult = articlePortalService.queryIndexArticleESList(keyword, category, page, pageSize);
        return CommonResponse.ok(pagedResult);
    }

    @Override
    public CommonResponse hotList() {
        return CommonResponse.ok(articlePortalService.queryESHotListByScore());
    }

    @Override
    public CommonResponse detail(String articleId) {

        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);
        // 获取用户信息列表
        List<UserBasicInfoVO> publisherList = getPublisherList(articleDetailVO.getPublishUserId());

        if (!publisherList.isEmpty()) {
            articleDetailVO.setPublishUserName(publisherList.get(0).getNickname());
        }

        articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId));

        return CommonResponse.ok(articleDetailVO);
    }

    @Override
    public CommonResponse detailContainsRegularlyPublishedArticles(String articleId) {

        ArticleDetailVO articleDetailVO = articlePortalService.queryDetailContainsRegularlyPublishedArticles(articleId);
        List<UserBasicInfoVO> publisherList = getPublisherList(articleDetailVO.getPublishUserId());

        if (!publisherList.isEmpty()) {
            articleDetailVO.setPublishUserName(publisherList.get(0).getNickname());
        }

        articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId));

        return CommonResponse.ok(articleDetailVO);
    }

    @Override
    public Integer readCounts(String articleId) {
        return getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId);
    }

    @Override
    public CommonResponse readArticle(String articleId, HttpServletRequest request) {

        String userIp = IPUtils.getRequestIp(request);

        // 设置针对当前用户ip的永久存在的key，存入到redis，表示该ip的用户已经阅读过了，第二次会被拦截器拦截不会进入这个方法，不再累加阅读量
        redis.setnx(REDIS_ALREADY_READ + ":" + articleId + ":" + userIp, userIp);
        // 阅读数累加，如果key不存在，那么它的值会先被初始化为0，然后再执行INCR命令
        redis.increment(REDIS_ARTICLE_READ_COUNTS + ":" + articleId, 1);
        // 累加zset中这篇文章的阅读数，当key不存在，或member不是key的成员时，ZINCRBY key increment member等同于ZADD key increment member
        redisTemplate.opsForZSet().incrementScore(ARTICLE_READ_COUNTS_ZSET, articleId, 1);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        if (page == null) {
            page = START_PAGE;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult pagedResult = articlePortalService.queryArticleListOfWriter(writerId, page, pageSize);
        return CommonResponse.ok(pagedResult);
    }

    @Override
    public CommonResponse queryGoodArticleListOfWriter(String writerId) {
        return CommonResponse.ok(articlePortalService.queryGoodArticleListOfWriter(writerId));
    }

    /**
     * 发起http远程调用，获取用户基本信息
     * @return
     */
    private List<UserBasicInfoVO> getPublisherList(String... id) {
        Set<String> idSet = new HashSet<>();
        for (String s : id) {
            idSet.add(s);
        }
        CommonResponse bodyResult = userMicroservice.queryByIds(JsonUtils.objectToJson(idSet));

        List<UserBasicInfoVO> publisherList = null;
        if (bodyResult.getCode() == HttpStatus.SC_OK) {

            String userJson = JsonUtils.objectToJson(bodyResult.getData());
            publisherList = JsonUtils.jsonToList(userJson, UserBasicInfoVO.class);
        } else {
            publisherList = new ArrayList<>();
        }

        return publisherList;
    }
}
