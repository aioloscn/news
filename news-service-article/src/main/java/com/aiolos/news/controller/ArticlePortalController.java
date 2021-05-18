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

    public ArticlePortalController(ArticlePortalService articlePortalService, RestTemplate restTemplate, UserControllerApi userMicroservice) {
        this.articlePortalService = articlePortalService;
        this.restTemplate = restTemplate;
        this.userMicroservice = userMicroservice;
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
    public CommonResponse hotList() {
        return CommonResponse.ok(articlePortalService.queryHotList());
    }


    @Override
    public CommonResponse detail(String articleId) {

        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        articleDetailVO = articlePortalService.queryDetail(articleId);

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
