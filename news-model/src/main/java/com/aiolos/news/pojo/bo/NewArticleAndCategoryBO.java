package com.aiolos.news.pojo.bo;

import lombok.Data;
import java.util.Date;

/**
 * @author Aiolos
 * @date 2021/9/9 9:48 上午
 */
@Data
public class NewArticleAndCategoryBO {

    /**
     * 爬虫新闻的Id
     */
    private String newId;

    private String title;

    private String content;

    private Integer categoryId;

    private Integer articleType;

    private String articleCover;

    private Integer isAppoint;

    private Date publishTime;

    private String publishUserId;

    private Integer id;

    private String name;

    private String tagColor;
}
