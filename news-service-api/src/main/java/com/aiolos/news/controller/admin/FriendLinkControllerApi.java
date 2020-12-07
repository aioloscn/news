package com.aiolos.news.controller.admin;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.pojo.bo.SaveFriendLinkBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Aiolos
 * @date 2020/11/27 11:51 上午
 */
@Api(value = "首页友情链接维护", tags = "首页友情链接维护Controller")
@RequestMapping("/friendLinkMng")
public interface FriendLinkControllerApi {

    @ApiOperation(value = "新增或修改友情链接", notes = "新增或修改友情链接", httpMethod = "POST")
    @PostMapping("/saveOrUpdateFriendLink")
    CommonResponse saveOrUpdateFriendLink(@Valid @RequestBody SaveFriendLinkBO saveFriendLinkBO, BindingResult bindingResult) throws CustomizeException;

    @ApiOperation(value = "查询友情链接列表", notes = "查询友情链接列表", httpMethod = "POST")
    @PostMapping("/getFriendLinkList")
    CommonResponse getFriendLinkList();

    @ApiOperation(value = "删除友情链接", notes = "删除友情链接", httpMethod = "POST")
    @PostMapping("/delete")
    CommonResponse delete(@RequestParam String linkId);

    @ApiOperation(value = "门户端查询友情链接列表", notes = "门户端查询友情链接列表", httpMethod = "GET")
    @GetMapping("/portal/list")
    CommonResponse queryPortalAllFriendLinkList();
}
