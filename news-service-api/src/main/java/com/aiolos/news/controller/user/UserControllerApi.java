package com.aiolos.news.controller.user;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2020/9/20 1:02 下午
 */
@Api(value = "用户信息相关", tags = {"用户功能的controller"})      // 代表当前这个类会被Swagger2扫描到
@RequestMapping("/user")
public interface UserControllerApi {

    @ApiOperation(value = "获取用户基本信息", notes = "获取用户基本信息", httpMethod = "POST")
    @PostMapping("/getUserBasicInfo")
    CommonResponse getUserBasicInfo(@RequestParam String userId);

    @ApiOperation(value = "获取用户账号信息", notes = "获取用户账号信息", httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    CommonResponse getAccountInfo(@RequestParam String userId);

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("/updateAccountInfo")
    CommonResponse updateAccountInfo(@Valid @RequestBody UpdateUserInfoBO updateUserInfoBO, BindingResult bindingResult) throws CustomizeException;
}
