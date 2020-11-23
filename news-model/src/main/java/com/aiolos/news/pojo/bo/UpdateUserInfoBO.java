package com.aiolos.news.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/10/21 7:50 下午
 */
@Getter
@Setter
@ToString
public class UpdateUserInfoBO {

    @NotBlank(message = "用户ID不能为空")
    private String id;

    @NotBlank(message = "用户昵称不能为空")
    @Length(max = 12, message = "用户名不能超过12位")
    private String nickname;

    @NotBlank(message = "用户头像不能为空")
    private String face;

    @NotBlank(message = "真实姓名不能为空")
    private String realname;

    @Email
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotNull(message = "请选择性别")
    @Min(value = 0, message = "性别选择不正确")
    @Max(value = 1, message = "性别选择不正确")
    private Integer sex;

    @NotNull(message = "请选择出生日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")     // 解决前端日期字符串传到后端后，转换为Date类型
    private Date birthday;

    @NotBlank(message = "请选择省份")
    private String province;

    @NotBlank(message = "请选择城市")
    private String city;

    @NotBlank(message = "请选择地区")
    private String district;
}
