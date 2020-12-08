package com.aiolos.news.pojo.bo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author Aiolos
 * @date 2020/10/11 10:40 下午
 */
@Data
public class RegisterLoginBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;
}
