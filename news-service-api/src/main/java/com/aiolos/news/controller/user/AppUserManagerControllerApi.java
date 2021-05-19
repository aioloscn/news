package com.aiolos.news.controller.user;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2021/5/8 7:41 下午
 */
@Api(value = "用户管理相关接口定义", tags = "用户管理相关接口定义controller")
@RequestMapping("/appUser")
public interface AppUserManagerControllerApi {

    @ApiOperation(value = "查询网站所有用户", httpMethod = "POST")
    @PostMapping("/queryAll")
    CommonResponse queryAll(@RequestParam String nickname, @RequestParam Integer status, @RequestParam Date startDate,
                            @RequestParam Date endDate, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize);

    @ApiOperation(value = "查看用户详情", httpMethod = "POST")
    @PostMapping("/userDetail")
    CommonResponse userDetail(@RequestParam String userId);

    @ApiOperation(value = "冻结或解冻用户", httpMethod = "POST")
    @PostMapping("/freezeUserOrNot")
    CommonResponse freezeUserOrNot(@RequestParam String userId, @RequestParam Integer doStatus) throws CustomizeException;
}
