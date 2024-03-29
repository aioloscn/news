package com.aiolos.news.pojo.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Aiolos
 * @date 2021/6/6 1:23 下午
 */
@Data
public class CommentsVO {

    private String id;
    private String fatherId;
    private String articleId;
    private String commentUserId;
    private String commentUserNickname;
    private String commentUserFace;
    private String content;
    private Date createTime;
    private List<CommentSubVO> commentSubVOs;
}
