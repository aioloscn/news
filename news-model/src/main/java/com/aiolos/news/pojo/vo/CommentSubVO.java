package com.aiolos.news.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2021/9/30 2:24 下午
 */
@Data
public class CommentSubVO {
    private String CommentId;
    private String fatherId;
    private String replyId;
    private String articleId;
    private String commentUserId;
    private String commentUserNickname;
    private String commentUserFace;
    private String content;
    private Date createTime;
    private String quoteUserNickname;
    private String quoteContent;
}