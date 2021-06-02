package com.aiolos.news.pojo.eo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author Aiolos
 * @date 2021/6/1 11:02 上午
 */
@Document(indexName = "fans", type = "_doc")
@Data
public class FansEO {

    @Id
    private String id;

    @Field
    private String writerId;

    @Field
    private String fanId;

    @Field
    private String face;

    @Field
    private String fanNickname;

    @Field
    private Integer sex;

    @Field
    private String province;
}
