package com.aiolos.news.pojo.bo;

import javax.validation.constraints.NotBlank;

/**
 * @author Aiolos
 * @date 2020/10/11 10:40 下午
 */
public class RegisterLoginBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    @Override
    public String toString() {
        return "RegisterLoginBO{" +
                "mobile='" + mobile + '\'' +
                ", smsCode='" + smsCode + '\'' +
                '}';
    }
}
