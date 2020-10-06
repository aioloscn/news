package com.aiolos.news.controller;

import com.aiolos.news.BaseController;
import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.utils.IPUtils;
import com.aiolos.news.common.utils.SMSUtils;
import com.aiolos.news.controller.user.PassportControllerApi;
import com.aiolos.news.dao.AdminUserDao;
import com.aiolos.news.service.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Aiolos
 * @date 2020/9/28 10:37 下午
 */
@Slf4j
@RestController
public class PassportController extends BaseController implements PassportControllerApi {

    private final SMSUtils smsUtils;

    private final AdminUserService adminUserService;

    public PassportController(SMSUtils smsUtils, AdminUserService adminUserService) {
        this.smsUtils = smsUtils;
        this.adminUserService = adminUserService;
    }

    @Override
    public CommonResponse getSMSCode(String mobile, HttpServletRequest request) {

        adminUserService.testJDBC();

        // 获得用户ip
        String userIp = IPUtils.getRequestIp(request);

        // 根据用户的IP进行限制，限制用户在60秒内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        String code = String.valueOf((int) ((1 + Math.random()) * 1000000)).substring(1);
        smsUtils.sendSMS(mobile, code);
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return null;
    }
}
