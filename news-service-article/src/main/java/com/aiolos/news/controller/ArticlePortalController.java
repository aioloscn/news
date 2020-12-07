package com.aiolos.news.controller;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.article.ArticlePortalControllerApi;
import com.aiolos.news.service.ArticlePortalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aiolos
 * @date 2020/12/6 9:12 下午
 */
@Slf4j
@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {

    private final ArticlePortalService articlePortalService;

    public ArticlePortalController(ArticlePortalService articlePortalService) {
        this.articlePortalService = articlePortalService;
    }

    @Override
    public CommonResponse list(String keyword, Integer category, Integer page, Integer pageSize) {

        log.info("Enter function article/portal/list, parameter keyword: {}, category: {}, page: {}, pageSize: {}", keyword, category, page, pageSize);

        if (page == null) {
            page = START_PAGE;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult pagedResult = articlePortalService.queryIndexArticleList(keyword, category, page, pageSize);
        return CommonResponse.ok(pagedResult);
    }
}
