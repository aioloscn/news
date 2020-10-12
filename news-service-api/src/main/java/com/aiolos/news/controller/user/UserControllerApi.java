package com.aiolos.news.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Aiolos
 * @date 2020/9/20 1:02 下午
 */
@Api(value = "controller的标题", tags = {"用户功能的controller"})      // 代表当前这个类会被Swagger2扫描到
@RequestMapping("/user")
public interface UserControllerApi {

//    @ApiOperation(value = "xx方法的接口", notes = "xx方法的接口", httpMethod = "POST")
}
