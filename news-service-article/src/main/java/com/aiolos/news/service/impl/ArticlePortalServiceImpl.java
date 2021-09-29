package com.aiolos.news.service.impl;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ArticleReviewStatus;
import com.aiolos.news.common.enums.YesOrNo;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.dao.ArticleDao;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.eo.ArticleEO;
import com.aiolos.news.pojo.vo.ArticleDetailVO;
import com.aiolos.news.pojo.vo.IndexArticleVO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.ArticlePortalService;
import com.aiolos.news.service.BaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author Aiolos
 * @date 2020/12/7 8:32 上午
 */
@Service
public class ArticlePortalServiceImpl extends BaseService implements ArticlePortalService {

    private final ArticleDao articleDao;

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    private final UserControllerApi userMicroservice;

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final StringRedisTemplate redisTemplate;

    public ArticlePortalServiceImpl(ArticleDao articleDao, RestTemplate restTemplate, DiscoveryClient discoveryClient, UserControllerApi userMicroservice, ElasticsearchTemplate elasticsearchTemplate, StringRedisTemplate redisTemplate) {
        this.articleDao = articleDao;
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
        this.userMicroservice = userMicroservice;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PagedResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {

        /**
         * 查询首页文章的隐性查询条件：
         * isAppoint=0 即时发布，表示文章已经发布，或定时发布到点后已发布
         * isDelete=0 未删除，表示只能显示未删除的文章
         * articleStatus=3 审核通过，表示文章只有通过机审/人工审核之后才能显示
         */
        Article article = new Article();
        article.setIsAppoint(YesOrNo.NO.getType());
        article.setIsDelete(YesOrNo.NO.getType());
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.getType());

        if (category != null) {
            article.setCategoryId(category);
        }

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>(article);

        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("title", keyword);
        }

        queryWrapper.orderByDesc("publish_time");

        IPage<Article> articleIPage = new Page<>(page, pageSize);
        articleIPage = articleDao.selectPage(articleIPage, queryWrapper);
        return rebuildArticlePagedResult(articleIPage);
    }

    @Override
    public PagedResult queryIndexArticleESList(String keyword, Integer category, Integer page, Integer pageSize) {

        Integer current = page;
        /**
         * 1. 首页默认查询，不带参数
         * 2. 按照文章分类查询
         * 3. 按照关键字查询
         */
        // es的页面是从0开始计算的，所以在这里page需要-1
        if (page < 1) return null;
        page--;
        // 分页
        Pageable pageable = PageRequest.of(page, pageSize);

        AggregatedPage<ArticleEO> pagedArticle = null;
        if (StringUtils.isBlank(keyword) && category == null) {
            SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(pageable).withSort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)).build();
            pagedArticle = elasticsearchTemplate.queryForPage(query, ArticleEO.class);
        }
        if (StringUtils.isBlank(keyword) && category != null) {
            SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.termQuery("categoryId", category))
                    .withPageable(pageable).withSort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)).build();
            pagedArticle = elasticsearchTemplate.queryForPage(query, ArticleEO.class);
        }

        // 关键字搜索高亮展示
        String searchTitleField = "title";
        if (StringUtils.isNotBlank(keyword)) {
            String preTag = "<font color='red'>";
            String postTag = "</font>";
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(QueryBuilders.matchQuery(searchTitleField, keyword));
            if (category != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryId", category));
            }
            SearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder)
                    .withHighlightFields(new HighlightBuilder.Field(searchTitleField).preTags(preTag).postTags(postTag))
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(query, ArticleEO.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    List<ArticleEO> articleHighlightList = new ArrayList<>();
                    SearchHits hits = response.getHits();
                    hits.forEach(h -> {
                        HighlightField highlightField = h.getHighlightFields().get(searchTitleField);
                        String title = highlightField.getFragments()[0].toString();
                        // 获得其他字段数据，并重新封装
                        String id = h.getSourceAsMap().get("id").toString();
                        Integer categoryId = (Integer) h.getSourceAsMap().get("categoryId");
                        Integer articleType = (Integer) h.getSourceAsMap().get("articleType");
                        String articleCover = h.getSourceAsMap().get("articleCover").toString();
                        String publishUserId = h.getSourceAsMap().get("publishUserId").toString();
                        Long longDate = (Long) h.getSourceAsMap().get("publishTime");
                        Date publishTime = new Date(longDate);

                        ArticleEO articleEO = new ArticleEO();
                        articleEO.setId(id);
                        articleEO.setTitle(title);
                        articleEO.setCategoryId(categoryId);
                        articleEO.setArticleType(articleType);
                        articleEO.setArticleCover(articleCover);
                        articleEO.setPublishUserId(publishUserId);
                        articleEO.setPublishTime(publishTime);
                        articleHighlightList.add(articleEO);
                    });
                    return new AggregatedPageImpl<>((List<T>) articleHighlightList, pageable, response.getHits().getTotalHits());
                }

                @Override
                public <T> T mapSearchHit(SearchHit searchHit, Class<T> type) {
                    return null;
                }
            });
        }

        List<ArticleEO> articleEOList = pagedArticle.getContent();
        List<Article> articleList = new ArrayList<>();
        articleEOList.forEach(a -> {
            Article article = new Article();
            BeanUtils.copyProperties(a, article);
            articleList.add(article);
        });

        IPage<Article> articleIPage = new Page<>(current, pageSize);
        articleIPage.setRecords(articleList);
        articleIPage.setCurrent(++page);
        articleIPage.setPages(pagedArticle.getTotalPages());
        articleIPage.setTotal(pagedArticle.getTotalElements());
        return rebuildArticlePagedResult(articleIPage);
    }

    /**
     * 构建PagedResult数据，包含文章发布者信息和文章阅读量
     * @param articleIPage
     * @return
     */
    private PagedResult rebuildArticlePagedResult(IPage<Article> articleIPage) {
        List<Article> articleList = articleIPage.getRecords();
        // 1. 构建发布者ID列表
        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();

        for (Article a : articleList) {

            // 1.1 构建发布者id的set
            idSet.add(a.getPublishUserId());
            // 1.2 构建文章id的list
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + a.getId());
        }

        // 发起redis的mget批量查询api，获得对应的值
        List<String> readCountsRedisList = redis.mget(idList);

        // 2. 发起远程调用（restTemplate），请求用户服务获得用户（idSet 发布者）的列表

        // 第一种远程调用接口方式，硬编码方式
//        String userServerUrlExecute = "http://www.aiolos.com:8003/news/user/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);
//        ResponseEntity<CommonResponse> responseEntity = restTemplate.getForEntity(userServerUrlExecute, CommonResponse.class);

//        String serviceId = "NEWS-USER";
        // 第二种远程调用接口方式，用discovery
//        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
//        ServiceInstance userService = serviceInstanceList.get(0);

        //        String userServerUrlExecute = "http://" + userService.getHost() + ":" + userService.getPort()
//                + "/news/user/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);

        // 第二种的优化，用serviceId替换userService.getHost()
//        String userServerUrlExecute = "http://" + serviceId+ "/news/user/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);

//        ResponseEntity<CommonResponse> responseEntity = restTemplate.getForEntity(userServerUrlExecute, CommonResponse.class);
//        CommonResponse bodyResult = responseEntity.getBody();

        // 第三种远程调用接口方式，生产者Api上加上@FeignClient注解，消费者启动程序上加@EnableFeignClients注解
        CommonResponse bodyResult = userMicroservice.queryByIds(JsonUtils.objectToJson(idSet));

        List<UserBasicInfoVO> publisherList = null;

        if (bodyResult.getCode().equals(HttpStatus.OK.value())) {

            String userJson = JsonUtils.objectToJson(bodyResult.getData());
            publisherList = JsonUtils.jsonToList(userJson, UserBasicInfoVO.class);
        }

        // 3. 拼接两个List，重组文章列表
        List<IndexArticleVO> indexArticleVOList = new ArrayList<>();

        for (int i = 0; i < articleList.size(); i++) {

            Article a = articleList.get(i);
            IndexArticleVO indexArticleVO = new IndexArticleVO();
            BeanUtils.copyProperties(a, indexArticleVO);

            // 3.1 从publisherList中获得发布者的基本信息
            UserBasicInfoVO publisher = getUserIfPublisher(a.getPublishUserId(), publisherList);
            indexArticleVO.setPublisherVO(publisher);

            // 3.2 从redis里拿到当前文章的阅读数，赋值
            String redisCountsStr = readCountsRedisList.get(i);
            int redisCounts = 0;

            if (StringUtils.isNotBlank(redisCountsStr)) {
                redisCounts = Integer.valueOf(redisCountsStr);
            }

            indexArticleVO.setReadCounts(redisCounts);
            indexArticleVOList.add(indexArticleVO);
        }

        PagedResult pagedResult = setterPagedResult(articleIPage);

        // 用拼接后的List替换原有的ArticleList
        pagedResult.setRecords(indexArticleVOList);
        return pagedResult;
    }

    @Override
    public List<Article> queryHotList() {

        /**
         * 查询首页文章的隐性查询条件：
         * isAppoint=0 即时发布，表示文章已经发布，或定时发布到点后已发布
         * isDelete=0 未删除，表示只能显示未删除的文章
         * articleStatus=3 审核通过，表示文章只有通过机审/人工审核之后才能显示
         */
        Article article = new Article();
        article.setIsAppoint(YesOrNo.NO.getType());
        article.setIsDelete(YesOrNo.NO.getType());
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.getType());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>(article);
        queryWrapper.orderByDesc("publish_time");

        IPage<Article> articleIPage = new Page<>(1, 5);
        articleIPage = articleDao.selectPage(articleIPage, queryWrapper);

        List<Article> articleList = articleIPage.getRecords();
        return articleList;
    }

    @Override
    public List<Article> queryESHotListByScore() {
        Set<String> articleIds = redisTemplate.opsForZSet().reverseRange(ARTICLE_READ_COUNTS_ZSET, 0, 5);
        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery();
        queryBuilder.ids().addAll(articleIds);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        List<ArticleEO> articleEOs = elasticsearchTemplate.queryForList(searchQuery, ArticleEO.class);
        // 撤销和删除的文章会从ES中删除，所以不在热门里显示
        List<Article> articles = new ArrayList<>();
        // es获得的数据只是按匹配到数据取出来，需要重新排序
        Iterator<String> iterator = articleIds.iterator();
        while (iterator.hasNext()) {
            String articleId = iterator.next();
            articleEOs.forEach(a -> {
                if (a.getId().equals(articleId)) {
                    Article article = new Article();
                    BeanUtils.copyProperties(a, article);
                    articles.add(article);
                }
            });
        }
        return articles;
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {
        Article article = new Article();
        article.setId(articleId);
//        article.setIsAppoint(YesOrNo.NO.getType());
        article.setIsDelete(YesOrNo.NO.getType());
//        article.setArticleStatus(ArticleReviewStatus.SUCCESS.getType());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>(article);
        article = articleDao.selectOne(queryWrapper);

        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        if (article != null) {
            BeanUtils.copyProperties(article, articleDetailVO);
        }
        return articleDetailVO;
    }

    @Override
    public ArticleDetailVO queryDetailContainsRegularlyPublishedArticles(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.NO.getType());
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.getType());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>(article);
        article = articleDao.selectOne(queryWrapper);

        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        if (article != null) {
            BeanUtils.copyProperties(article, articleDetailVO);
        }
        return articleDetailVO;
    }

    @Override
    public PagedResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publish_user_id", writerId);
        queryWrapper.eq("is_delete", YesOrNo.NO.getType());
        queryWrapper.orderByDesc("create_time");
        IPage<Article> articleIPage = new Page<>(page, pageSize);
        articleIPage = articleDao.selectPage(articleIPage, queryWrapper);
        return rebuildArticlePagedResult(articleIPage);
    }

    @Override
    public List<Article> queryGoodArticleListOfWriter(String writerId) {
        Set<String> articleIds = redisTemplate.opsForZSet().reverseRange(ARTICLE_READ_COUNTS_ZSET, 0, 5);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("publishUserId", writerId));

        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery();
        idsQueryBuilder.ids().addAll(articleIds);
        boolQueryBuilder.must(idsQueryBuilder);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        List<ArticleEO> articleEOs = elasticsearchTemplate.queryForList(searchQuery, ArticleEO.class);
        // 撤销和删除的文章会从ES中删除，所以不在近期佳文里显示
        List<Article> articles = new ArrayList<>();
        if (articleEOs == null || articleEOs.size() == 0)
            return articles;

        // es获得的数据只是按匹配到数据取出来，需要重新排序
        Iterator<String> iterator = articleIds.iterator();
        while (iterator.hasNext()) {
            String articleId = iterator.next();
            articleEOs.forEach(a -> {
                if (a.getId().equals(articleId)) {
                    Article article = new Article();
                    BeanUtils.copyProperties(a, article);
                    articles.add(article);
                }
            });
        }

        // 根据文章ids批量获取文章阅读量
        List<String> idList = new ArrayList<>();
        articles.forEach(a -> {
            // 构建文章id的list
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + a.getId());
        });
        List<String> readCountsRedisList = redis.mget(idList);
        for (int i = 0; i < articles.size(); i++) {
            Integer readCounts = readCountsRedisList.get(i) == null ? 0 : Integer.valueOf(readCountsRedisList.get(i));
            articles.get(i).setReadCounts(readCounts);
        }
        return articles;
    }

    /**
     * 文章列表中拿出发布者ID，在发布者基本信息列表中匹配
     * @param publisherId       发布者ID
     * @param publisherList     发布者基本信息列表
     * @return
     */
    private UserBasicInfoVO getUserIfPublisher(String publisherId, List<UserBasicInfoVO> publisherList) {
        if (StringUtils.isBlank(publisherId) || publisherList == null)
            return new UserBasicInfoVO();
        for (UserBasicInfoVO userBasicInfoVO : publisherList) {
            if (userBasicInfoVO.getId().equalsIgnoreCase(publisherId)) {
                return userBasicInfoVO;
            }
        }
        return new UserBasicInfoVO();
    }
}
