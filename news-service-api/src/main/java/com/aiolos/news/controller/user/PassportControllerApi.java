package com.aiolos.news.controller.user;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.pojo.bo.RegisterLoginBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2020/9/28 10:32 下午
 */
@Api(value = "用户注册登录", tags = {"用户注册登录的controller"})
@RequestMapping("/passport")
public interface PassportControllerApi {

    @ApiOperation(value = "获得短信验证码接口", notes = "获得短信验证码接口", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    CommonResponse getSMSCode(String mobile, HttpServletRequest request) throws CustomizeException;

    @ApiOperation(value = "一键注册登录接口", notes = "一键注册登录接口", httpMethod = "POST")
    @PostMapping("/login")
    CommonResponse login(@Valid @RequestBody RegisterLoginBO registerLoginBO, BindingResult result) throws CustomizeException;
}
