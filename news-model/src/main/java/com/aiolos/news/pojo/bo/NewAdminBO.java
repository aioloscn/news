package com.aiolos.news.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Aiolos
 * @date 2020/11/14 8:45 下午
 */
@Getter
@Setter
@ToString
public class NewAdminBO {

    private String username;
    private String adminName;
    private String password;
    private String confirmPassword;
    private String img64;
    private String faceId;
}
