package com.aiolos.news.pojo.mo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/11/27 6:33 下午
 */
@Getter
@Setter
@ToString
@Document("FriendLink")
public class FriendLinkMO {

    @Id
    private String id;

    @Field("link_name")
    private String linkName;

    @Field("link_url")
    private String link_url;

    @Field("is_delete")
    private String isDelete;

    @Field("create_time")
    private Date createTime;

    @Field("update_time")
    private Date updateTime;
}
