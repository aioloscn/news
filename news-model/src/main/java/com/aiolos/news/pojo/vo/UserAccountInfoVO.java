package com.aiolos.news.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/10/21 4:30 下午
 */
@Data
public class UserAccountInfoVO {

    private String id;

    private String mobile;

    private String nickname;

    private String face;

    private String realname;

    private String email;

    private Integer sex;

    private Date birthday;

    private String province;

    private String city;

    private String district;
}
