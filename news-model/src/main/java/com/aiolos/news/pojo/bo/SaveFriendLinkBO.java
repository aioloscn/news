package com.aiolos.news.pojo.bo;

import com.aiolos.news.pojo.validate.CheckUrl;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Aiolos
 * @date 2020/11/27 11:54 上午
 */
@Data
public class SaveFriendLinkBO {

    private String id;

    @NotBlank(message = "友情链接名不能为空")
    private String linkName;

    @NotBlank(message = "友情链接地址不能为空")
    @CheckUrl
    private String linkUrl;

    @NotNull(message = "请选择保留或删除")
    private Integer isDelete;
}
