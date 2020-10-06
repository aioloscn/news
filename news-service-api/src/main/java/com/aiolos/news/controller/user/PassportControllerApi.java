package com.aiolos.news.controller.user;

import com.aiolos.news.common.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Aiolos
 * @date 2020/9/28 10:32 下午
 */
@Api(value = "用户注册登录", tags = {"用户注册登录的controller"})
@RequestMapping("/passport")
public interface PassportControllerApi {

    @ApiOperation(value = "获得短信验证码", notes = "获得短信验证码", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    CommonResponse getSMSCode(String mobile, HttpServletRequest request);
}
