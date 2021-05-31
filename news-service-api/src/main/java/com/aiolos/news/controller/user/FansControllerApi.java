package com.aiolos.news.controller.user;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Aiolos
 * @date 2021/5/13 4:11 上午
 */
@Api(value = "粉丝管理", tags = "粉丝管理controller")
@RequestMapping("/fans")
public interface FansControllerApi {

    @ApiOperation(value = "查询当前用户是否关注了该作家", httpMethod = "POST")
    @PostMapping("/isMeFollowThisWriter")
    CommonResponse isMeFollowThisWriter(@RequestParam String writerId, @RequestParam String fanId);

    @ApiOperation(value = "用户关注作家", httpMethod = "POST")
    @PostMapping("/follow")
    CommonResponse follow(@RequestParam String writerId, @RequestParam String fanId) throws CustomizedException;

    @ApiOperation(value = "用户取消关注", httpMethod = "POST")
    @PostMapping("/unfollow")
    CommonResponse unfollow(@RequestParam String writerId, @RequestParam String fanId) throws CustomizedException;

    @ApiOperation(value = "查询粉丝列表", httpMethod = "POST")
    @PostMapping("/queryAll")
    CommonResponse queryAll(@RequestParam String writerId,
                            @ApiParam(name = "page", value = "查询第几页", required = false) @RequestParam Integer page,
                            @ApiParam(name = "pageSize", value = "每页显示条数", required = false) @RequestParam Integer pageSize);

    @ApiOperation(value = "查询男女粉丝数量", httpMethod = "POST")
    @PostMapping("/queryRatio")
    CommonResponse queryRatio(@RequestParam String writerId);

    @ApiOperation(value = "根据地域查询粉丝数量", httpMethod = "POST")
    @PostMapping("/queryRatioByRegion")
    CommonResponse queryRatioByRegion(@RequestParam String writerId);
}
