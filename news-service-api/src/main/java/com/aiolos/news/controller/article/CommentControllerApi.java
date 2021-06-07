package com.aiolos.news.controller.article;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.pojo.bo.CommentReplyBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2021/6/5 10:37 上午
 */
@Api(tags = "文章评论相关业务的controller")
@RequestMapping("/comment")
public interface CommentControllerApi {

    @ApiOperation(value = "用户评论", httpMethod = "POST")
    @PostMapping("/createComment")
    CommonResponse createComment(@Valid @RequestBody CommentReplyBO commentReplyBO) throws CustomizedException;

    @ApiOperation(value = "查询文章评论数", httpMethod = "GET")
    @GetMapping("/counts")
    CommonResponse counts(@ApiParam(name = "articleId", value = "文章主键Id", required = true) @RequestParam String articleId);

    @ApiOperation(value = "查询文章的所有评论", httpMethod = "GET")
    @GetMapping("/list")
    CommonResponse list(@ApiParam(name = "articleId", value = "文章主键Id", required = true) @RequestParam String articleId,
                        @ApiParam(name = "page", value = "第几页") @RequestParam(required = false) Integer page,
                        @ApiParam(name = "pageSize", value = "每页显示数") @RequestParam(required = false) Integer pageSize);
}
