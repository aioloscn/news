package com.aiolos.news.pojo.vo;

import lombok.Data;

/**
 * @author Aiolos
 * @date 2020/10/22 7:20 下午
 */
@Data
public class UserBasicInfoVO {

    private String id;

    private String nickname;

    private String face;

    private Integer activeStatus;

    private Integer myFollowCounts;

    private Integer myFansCounts;
}
