package com.aiolos.news.controller.user;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.config.MyServiceList;
import com.aiolos.news.controller.user.fallbacks.UserControllerFallbackFactory;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2020/9/20 1:02 下午
 */
@Api(value = "用户信息相关", tags = {"用户功能的controller"})      // 代表当前这个类会被Swagger2扫描到
@RequestMapping("/user")
@FeignClient(value = MyServiceList.NEWS_USER, fallbackFactory = UserControllerFallbackFactory.class)       // 服务提供者
public interface UserControllerApi {

    @ApiOperation(value = "获取用户基本信息", notes = "获取用户基本信息", httpMethod = "POST")
    @PostMapping("/getUserBasicInfo")
    CommonResponse getUserBasicInfo(@RequestParam String userId);

    @ApiOperation(value = "获取用户账号信息", notes = "获取用户账号信息", httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    CommonResponse getAccountInfo(@RequestParam String userId);

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("/updateAccountInfo")
    CommonResponse updateAccountInfo(@Valid @RequestBody UpdateUserInfoBO updateUserInfoBO) throws CustomizedException;

    @ApiOperation(value = "根据多个用户的id查询用户列表", notes = "根据多个用户的id查询用户列表", httpMethod = "GET")
    @GetMapping("/queryByIds")
    CommonResponse queryByIds(@RequestParam String userIds);
}
