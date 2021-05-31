package com.aiolos.news.controller;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.IPUtils;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.article.ArticlePortalControllerApi;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.pojo.eo.ArticleEO;
import com.aiolos.news.pojo.vo.ArticleDetailVO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.ArticlePortalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ArticlePortalController(ArticlePortalService articlePortalService, RestTemplate restTemplate, UserControllerApi userMicroservice, ElasticsearchTemplate elasticsearchTemplate) {
        this.articlePortalService = articlePortalService;
        this.restTemplate = restTemplate;
        this.userMicroservice = userMicroservice;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public CommonResponse list(String keyword, Integer category, Integer page, Integer pageSize) {

        log.info("Enter the method article/portal/list, parameter keyword: {}, category: {}, page: {}, pageSize: {}", keyword, category, page, pageSize);

        if (page == null) {
            page = START_PAGE;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult pagedResult = articlePortalService.queryIndexArticleList(keyword, category, page, pageSize);
        return CommonResponse.ok(pagedResult);
    }

    @Override
    public CommonResponse esList(String keyword, Integer category, Integer page, Integer pageSize) {
        /**
         * 1. 首页默认查询，不带参数
         * 2. 按照文章分类查询
         * 3. 按照关键字查询
         */
        // es的页面是从0开始计算的，所以在这里page需要-1
        if (page < 1) return null;
        page--;
        Pageable pageable = PageRequest.of(page, pageSize);

        SearchQuery query = null;
        if (StringUtils.isBlank(keyword) && category == null) {
            query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).withPageable(pageable).build();
        }
        if (StringUtils.isBlank(keyword) && category != null) {
            query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.termQuery("categoryId", category)).build();
        }
        if (StringUtils.isNotBlank(keyword) && category == null) {
            query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("title", keyword)).build();
        }

        AggregatedPage<ArticleEO> pagedArticle = elasticsearchTemplate.queryForPage(query, ArticleEO.class);
        List<ArticleEO> articleList = pagedArticle.getContent();
        return CommonResponse.ok(articleList);
    }

    @Override
    public CommonResponse hotList() {
        return CommonResponse.ok(articlePortalService.queryHotList());
    }

    @Override
    public CommonResponse detail(String articleId) {

        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);
        Set<String> idSet = new HashSet<>();
        idSet.add(articleDetailVO.getPublishUserId());
        List<UserBasicInfoVO> publisherList = getPublisherList(idSet);

        if (!publisherList.isEmpty()) {
            articleDetailVO.setPublishUserName(publisherList.get(0).getNickname());
        }

        articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId));

        return CommonResponse.ok(articleDetailVO);
    }

    @Override
    public CommonResponse detailContainsRegularlyPublishedArticles(String articleId) {

        ArticleDetailVO articleDetailVO = articlePortalService.queryDetailContainsRegularlyPublishedArticles(articleId);
        Set<String> idSet = new HashSet<>();
        idSet.add(articleDetailVO.getPublishUserId());
        List<UserBasicInfoVO> publisherList = getPublisherList(idSet);

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

        redis.increment(REDIS_ARTICLE_READ_COUNTS + ":" + articleId, 1);
        return CommonResponse.ok();
    }

    /**
     * 发起restTemplate远程调用，获取用户基本信息
     * @return
     */
    private List<UserBasicInfoVO> getPublisherList(Set<String> idSet) {

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
