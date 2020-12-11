package com.aiolos.news.service.impl;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.enums.ArticleReviewStatus;
import com.aiolos.news.common.enums.YesOrNo;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.dao.ArticleDao;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.vo.ArticleDetailVO;
import com.aiolos.news.pojo.vo.IndexArticleVO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.ArticlePortalService;
import com.aiolos.news.service.BaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Aiolos
 * @date 2020/12/7 8:32 上午
 */
@Service
public class ArticlePortalServiceImpl extends BaseService implements ArticlePortalService {

    private final ArticleDao articleDao;

    private final RestTemplate restTemplate;

    private final UserControllerApi userMicroservice;

    public ArticlePortalServiceImpl(ArticleDao articleDao, RestTemplate restTemplate, UserControllerApi userMicroservice) {
        this.articleDao = articleDao;
        this.restTemplate = restTemplate;
        this.userMicroservice = userMicroservice;
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
        article.setIsAppoint(YesOrNo.NO.type);
        article.setIsDelete(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);

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

        // 第三种远程调用接口方式，生产者Api上加上@FeignClient注解，消费者启动程序上加@EnableFeignClient注解
        CommonResponse bodyResult = userMicroservice.queryByIds(JsonUtils.objectToJson(idSet));

        List<UserBasicInfoVO> publisherList = null;

        if (bodyResult.getCode() == 200) {

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
//        for (Article a : articleList) {
//
//            IndexArticleVO indexArticleVO = new IndexArticleVO();
//            BeanUtils.copyProperties(a, indexArticleVO);
//
//            // 3.1 从publisherList中获得发布者的基本信息
//            UserBasicInfoVO publisher = getUserIfPublisher(a.getPublishUserId(), publisherList);
//            indexArticleVO.setPublisherVO(publisher);
//
//            // 3.2 从redis里拿到当前文章的阅读数，赋值
//            int readCounts = getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + a.getId());
//            indexArticleVO.setReadCounts(readCounts);
//
//            indexArticleVOList.add(indexArticleVO);
//        }

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
        article.setIsAppoint(YesOrNo.NO.type);
        article.setIsDelete(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>(article);

        queryWrapper.orderByDesc("publish_time");

        IPage<Article> articleIPage = new Page<>(1, 5);
        articleIPage = articleDao.selectPage(articleIPage, queryWrapper);

        List<Article> articleList = articleIPage.getRecords();
        return articleList;
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {

        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(YesOrNo.NO.type);
        article.setIsDelete(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>(article);
        article = articleDao.selectOne(queryWrapper);

        ArticleDetailVO articleDetailVO = new ArticleDetailVO();

        if (article != null) {
            BeanUtils.copyProperties(article, articleDetailVO);
        }

        return articleDetailVO;
    }

    /**
     * 文章列表中拿出发布者ID，在发布者基本信息列表中匹配
     * @param publisherId       发布者ID
     * @param publisherList     发布者基本信息列表
     * @return
     */
    private UserBasicInfoVO getUserIfPublisher(String publisherId, List<UserBasicInfoVO> publisherList) {

        for (UserBasicInfoVO userBasicInfoVO : publisherList) {
            if (userBasicInfoVO.getId().equalsIgnoreCase(publisherId)) {
                return userBasicInfoVO;
            }
        }

        return null;
    }
}