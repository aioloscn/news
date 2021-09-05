package com.aiolos.news.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Aiolos
 * @date 2021/5/10 1:26 上午
 */
@Data
@ApiModel(value = "保存或修改分类对象")
public class SaveCategoryBO {

    @ApiModelProperty(value = "主键", notes = "为空则新增，不为空则修改")
    private Integer id;

    @ApiModelProperty(value = "分类名称", required = true)
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @ApiModelProperty(value = "旧的名称")
    private String oldName;

    @ApiModelProperty(value = "分类颜色", required = true)
    @NotBlank(message = "分类颜色不能为空")
    private String tagColor;
}
