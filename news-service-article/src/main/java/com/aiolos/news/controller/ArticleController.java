package com.aiolos.news.controller;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ArticleCoverType;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.controller.article.ArticleControllerApi;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleBO;
import com.aiolos.news.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/26 6:27 上午
 */
@Slf4j
@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    public final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public CommonResponse createArticle(@Valid NewArticleBO newArticleBO) throws CustomizeException {

        log.info("Enter the method createArticle, parameter newArticleBO: {}", newArticleBO.toString());

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
        String allCatsJson = redis.get(REDIS_ALL_CATEGORY);
        if (StringUtils.isBlank(allCatsJson)) {
            return CommonResponse.error(ErrorEnum.SYSTEM_OPERATION_ERROR);
        } else {

            List<Category> categoryList = JsonUtils.jsonToList(allCatsJson, Category.class);
            for (Category c : categoryList) {

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

        log.info("Enter the method queryMyArticleList, parameter userId: {}, keyword: {}, status: {}, pageNum: {}, pageSize: {}",
                                    userId, keyword, status, pageNum, pageSize);

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
}
