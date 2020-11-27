package com.aiolos.news.controller.article;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.pojo.bo.NewArticleBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/11/26 6:20 上午
 */
@Api(value = "文章业务维护", tags = {"文章业务内容的controller"})
@RequestMapping("/article")
public interface ArticleControllerApi {

    @ApiOperation(value = "用户发布文章", notes = "用户发布文章", httpMethod = "POST")
    @PostMapping("/createArticle")
    CommonResponse createArticle(@Valid @RequestBody NewArticleBO newArticleBO, BindingResult bindingResult) throws CustomizeException;

    @ApiOperation(value = "查询用户自己的所有文章", notes = "查询用户自己的所有文章", httpMethod = "POST")
    @PostMapping("/queryMyArticleList")
    CommonResponse queryMyArticleList(@RequestParam String userId, @RequestParam String keyword, @RequestParam Integer status,
                                      @RequestParam Date startDate, @RequestParam Date endDate, @RequestParam Integer pageNum, @RequestParam Integer pageSize);
}
