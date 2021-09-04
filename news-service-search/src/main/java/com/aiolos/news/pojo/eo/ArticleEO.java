package com.aiolos.news.pojo.eo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2021/5/25 6:00 下午
 */
@Document(indexName = "articles", type = "_doc")
@Data
public class ArticleEO {

    @Id
    private String id;

    @Field
    private String title;

    @Field
    private Integer categoryId;

    @Field
    private Integer articleType;

    @Field
    private String articleCover;

    @Field
    private Integer isAppoint;

    @Field
    private Integer articleStatus;

    @Field
    private String publishUserId;

    @Field
    private Date publishTime;
}
