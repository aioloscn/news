package com.aiolos.news.controller;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.UserStatus;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.utils.IPUtils;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.SMSUtils;
import com.aiolos.news.controller.user.PassportControllerApi;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.bo.RegisterLoginBO;
import com.aiolos.news.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Aiolos
 * @date 2020/9/28 10:37 下午
 */
@Slf4j
@RestController
public class PassportController extends BaseController implements PassportControllerApi {

    private final SMSUtils smsUtils;

    private final UserService userService;

    public PassportController(SMSUtils smsUtils, UserService  userService) {
        this.smsUtils = smsUtils;
        this.userService = userService;
    }

    @Override
    public CommonResponse getSMSCode(String mobile, HttpServletRequest request) throws CustomizedException {

        if (StringUtils.isBlank(mobile)) {
            return CommonResponse.error(ErrorEnum.PHONE_INCORRECT);
        }

        // 获得用户ip
        String userIp = IPUtils.getRequestIp(request);

        // 根据用户的IP进行限制，限制用户在60秒内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        String code = String.valueOf((int) ((1 + Math.random()) * 1000000)).substring(1);
        log.info("code: {}", code);
        smsUtils.sendSMS(mobile, code);
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse login(RegisterLoginBO registerLoginBO,
                                HttpServletRequest request, HttpServletResponse response) throws CustomizedException {

        String mobile = registerLoginBO.getMobile();
        String smsCode = registerLoginBO.getSmsCode();

        // 1. 校验验证码是否匹配
        String redisSMSCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisSMSCode)) {
            return CommonResponse.error(ErrorEnum.SMS_CODE_EXPIRED);
        }
        if (!redisSMSCode.equalsIgnoreCase(smsCode)) {
            return CommonResponse.error(ErrorEnum.SMS_CODE_INCORRECT);
        }

        // 2. 查询数据库，判断该用户是否已注册
        AppUser user = userService.queryMobileIsExist(mobile);
        if (user != null && user.getActiveStatus().equals(UserStatus.FROZEN.getType())) {
            // 如果用户已注册并且状态为冻结，则直接抛出异常，禁止登录
            return CommonResponse.error(ErrorEnum.ACCOUNT_FROZEN);
        } else if (user == null) {
            // 如果用户没有注册过，则为null，需要注册
            user = userService.creatUser(mobile);
        }

        // 3. 保存用户分布式会话的相关操作
        int userActiveStatus = user.getActiveStatus();
        if (userActiveStatus != UserStatus.FROZEN.getType()) {

            String utoken = UUID.randomUUID().toString();
            log.info("token: {}", utoken);
            // 保存用户的会话信息
            redis.set(REDIS_USER_TOKEN + ":" + user.getId(), utoken, COOKIE_EXPIRE_TIME);
            // 保存用户账号信息
            redis.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user), COOKIE_EXPIRE_TIME);

            // 保存用户的ID和token到cookie中
            setCookie("uid", user.getId(), COOKIE_EXPIRE_TIME, request, response);
            setCookie("utoken", utoken, COOKIE_EXPIRE_TIME, request, response);
        }

        // 4. 用户登录或注册成功以后，需要删除redis中的短信验证码，验证码只能使用一次
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 5. 返回用户状态
        return CommonResponse.ok(userActiveStatus);
    }

    @Override
    public CommonResponse logout(String userId, HttpServletRequest request, HttpServletResponse response) {

        redis.del(REDIS_USER_TOKEN + ":" + userId);
        deleteCookieValue("uid", request, response);
        deleteCookieValue("utoken", request, response);
        return CommonResponse.ok();
    }
}
