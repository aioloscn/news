package com.aiolos.news.controller.admin;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.pojo.bo.AdminLoginBO;
import com.aiolos.news.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2020/11/14 10:46 上午
 */
@Api(value = "管理员维护", tags = {"管理员维护的controller"})
@RequestMapping("/admin")
public interface AdminControllerApi {

    @ApiOperation(value = "admin登录接口", notes = "admin登录接口", httpMethod = "POST")
    @PostMapping("/adminLogin")
    CommonResponse adminLogin(@Valid @RequestBody AdminLoginBO adminLoginBO, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws CustomizeException;

    @ApiOperation(value = "查询admin用户名是否存在", notes = "查询admin用户名是否存在", httpMethod = "POST")
    @PostMapping("/adminIsExist")
    CommonResponse adminIsExist(@RequestParam String username) throws CustomizeException;

    @ApiOperation(value = "创建admin", notes = "创建admin", httpMethod = "POST")
    @PostMapping("/addNewAdmin")
    CommonResponse addNewAdmin(@Valid @RequestBody NewAdminBO newAdminBO) throws CustomizeException;

    @ApiOperation(value = "查询admin列表", notes = "查询admin列表", httpMethod = "POST")
    @PostMapping("/getAdminList")
    CommonResponse getAdminList(@ApiParam(name = "pageNum", value = "查询第几页", required = false) @RequestParam(required = false) Integer pageNum,
                                @ApiParam(name = "pageSize", value = "分页查询每一页显示的条数", required = false) @RequestParam(required = false) Integer pageSize);

    @ApiOperation(value = "admin注销登录", notes = "admin注销登录", httpMethod = "POST")
    @PostMapping("/adminLogout")
    CommonResponse adminLogout(@RequestParam String adminId, HttpServletRequest request, HttpServletResponse response);
}
