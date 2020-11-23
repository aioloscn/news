package com.aiolos.news.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author Aiolos
 * @date 2020/11/14 12:27 下午
 */
@Getter
@Setter
@ToString
public class AdminLoginBO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String img64;
}
