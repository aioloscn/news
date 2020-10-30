package com.aiolos.news.controller;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.CommonUtils;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;
import com.aiolos.news.pojo.vo.UserAccountInfoVO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import com.aiolos.news.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

        log.info("Enter function getUserBasicInfo, parameter userId: {}", userId);

        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return CommonResponse.error(ErrorEnum.USER_NOT_LOGGED_IN);
        }

        // 1. 根据userId查询用户信息
        AppUser user = getUser(userId);

        // 2.返回用户信息
        UserBasicInfoVO userBasicInfoVO = new UserBasicInfoVO();
        BeanUtils.copyProperties(user, userBasicInfoVO);
        return CommonResponse.ok(userBasicInfoVO);
    }

    @Override
    public CommonResponse getAccountInfo(String userId) {

        log.info("Enter function getAccountInfo, parameter userId: {}", userId);

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
    public CommonResponse updateAccountInfo(@Valid UpdateUserInfoBO updateUserInfoBO, BindingResult bindingResult) throws CustomizeException {

        log.info("Enter function updateAccountInfo, parameter updateUserInfoBO: {}", JsonUtils.objectToJson(updateUserInfoBO));

        if (bindingResult.hasErrors()) {
            throw new CustomizeException(ErrorEnum.PARAMETER_VALIDATION_ERROR, CommonUtils.processErrorString(bindingResult));
        }

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
            // 由于用户信息不怎么变动，对于千万级别的网站这类信息不会直接去查询数据库，可以把查询后的数据存入redis
            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }

        return user;
    }
}
