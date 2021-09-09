package com.aiolos.news.controller;

import cn.hutool.core.bean.BeanUtil;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;
import com.aiolos.news.pojo.vo.UserAccountInfoVO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aiolos
 * @date 2020/9/20 1:04 下午
 */
@Slf4j
@RestController
public class UserController extends BaseController implements UserControllerApi {

    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CommonResponse getUserBasicInfo(String userId) {

        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return CommonResponse.error(ErrorEnum.USER_NOT_LOGGED_IN);
        }

        UserBasicInfoVO userBasicInfoVO = getUserBasicInfoVO(userId);
        return CommonResponse.ok(userBasicInfoVO);
    }

    @Override
    public CommonResponse getAccountInfo(String userId) {

        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return CommonResponse.error(ErrorEnum.USER_NOT_LOGGED_IN);
        }

        // 1. 根据userId查询用户信息
        AppUser user = getUser(userId);

        // 2.返回用户信息
        UserAccountInfoVO userAccountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, userAccountInfoVO);
        return CommonResponse.ok(userAccountInfoVO);
    }

    @Override
    public CommonResponse updateAccountInfo(UpdateUserInfoBO updateUserInfoBO) throws CustomizedException {

        userService.updateAccountInfo(updateUserInfoBO);
        return CommonResponse.ok();
    }

    private AppUser getUser(String userId) {

        // 查询redis中是否包含用户信息，如果包含直接返回
        String userJson = redis.get(REDIS_USER_INFO + ":" + userId);
        AppUser user = null;
        if (StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        } else {
            user = userService.getUser(userId);
            // 由于用户信息不怎么变动，可以把查询后的数据存入redis
            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }

        return user;
    }

    @HystrixCommand
    @Override
    public CommonResponse queryByIds(String userIds) {

        if (StringUtils.isBlank(userIds)) {
            return CommonResponse.error(ErrorEnum.USER_DOES_NOT_EXIST);
        }

        List<UserBasicInfoVO> publisherList = new ArrayList<>();
        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);

        for (String userId : userIdList) {

            // 获得用户基本信息
            UserBasicInfoVO userBasicInfoVO = getUserBasicInfoVO(userId);

            // 3.添加到publisherList
            publisherList.add(userBasicInfoVO);
        }
        return CommonResponse.ok(publisherList);
    }

    @Override
    public UserBasicInfoVO getUserByName(String nickname) {
        AppUser user = userService.getUserByName(nickname);
        UserBasicInfoVO userBasicInfoVO = new UserBasicInfoVO();
        BeanUtil.copyProperties(user, userBasicInfoVO);
        return userBasicInfoVO;
    }

    private UserBasicInfoVO getUserBasicInfoVO(String userId) {

        // 1.根据userId查询用户信息
        AppUser user = getUser(userId);

        // 2.返回用户信息
        UserBasicInfoVO userBasicInfoVO = new UserBasicInfoVO();
        BeanUtils.copyProperties(user, userBasicInfoVO);

        // 3.查询redis中用户的关注数和粉丝数
        // 我的关注数
        Integer myFollowCounts = getCountsFromRedis(REDIS_MY_FOLLOW_COUNT + ":" + userId);
        // 我的粉丝数
        Integer myFansCounts = getCountsFromRedis(REDIS_WRITER_FANS_COUNT + ":" + userId);

        userBasicInfoVO.setMyFollowCounts(myFollowCounts);
        userBasicInfoVO.setMyFansCounts(myFansCounts);
        return userBasicInfoVO;
    }
}
