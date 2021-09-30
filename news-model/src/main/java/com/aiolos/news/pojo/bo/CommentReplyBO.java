package com.aiolos.news.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author Aiolos
 * @date 2021/6/5 11:33 上午
 */
@ApiModel(value = "文章评论和回复评论的对象")
@Data
public class CommentReplyBO {

    @ApiModelProperty(value = "文章主键", required = true)
    @NotBlank(message = "缺少文章相关信息")
    private String articleId;

    @ApiModelProperty(value = "上级评论Id", notes = "如果是一级评论则为0", required = true)
    @NotBlank(message = "缺少文章相关信息")
    private String fatherId;

    @ApiModelProperty(value = "所回复的评论Id", notes = "如果是一级评论则为0，主要用于记录楼中楼的评论Id", required = true)
    @NotBlank(message = "缺少文章相关信息")
    private String replyId;

    @ApiModelProperty(value = "评论者主键", required = true)
    @NotBlank(message = "当前用户信息不正确，请尝试重新登录")
    private String commentUserId;

    @ApiModelProperty(value = "留言内容", notes = "内容长度不能超过100字", required = true)
    @NotBlank(message = "留言内容不能为空")
    @Length(max = 100, message = "留言内容不能超过100字")
    private String content;
}
