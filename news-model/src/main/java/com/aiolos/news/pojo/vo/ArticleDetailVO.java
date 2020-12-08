package com.aiolos.news.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/12/8 10:13 上午
 */
@Data
public class ArticleDetailVO {

    private String id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章封面图，article_type=1 的时候展示
     */
    private String articleCover;

    /**
     * 文章所属分类id
     */
    private Integer categoryId;

    /**
     * 文章所属分类名称
     */
    private String categoryName;

    /**
     * 发布者用户id
     */
    private String publishUserId;

    /**
     * 发布者用户名称
     */
    private String publishUserName;

    /**
     * 文章发布时间（也是预约发布的时间）
     */
    private Date publishTime;

    /**
     * 文章内容，长度不超过9999，需要在前后端判断
     */
    private String content;

    /**
     * 阅读数
     */
    private Integer readCounts;
}
