package com.aiolos.news.controller;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.enums.UserStatus;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.controller.user.AppUserManagerControllerApi;
import com.aiolos.news.service.AppUserManagerService;
import com.aiolos.news.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2021/5/8 7:47 下午
 */
@Slf4j
@RestController
public class AppUserManagerController extends BaseController implements AppUserManagerControllerApi {

    private final AppUserManagerService appUserManagerService;

    private final UserService userService;

    public AppUserManagerController(AppUserManagerService appUserManagerService, UserService userService) {
        this.appUserManagerService = appUserManagerService;
        this.userService = userService;
    }

    @Override
    public CommonResponse queryAll(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        if (page == null) page = START_PAGE;
        if (pageSize == null) pageSize = PAGE_SIZE;
        return CommonResponse.ok(appUserManagerService.queryAllUserList(nickname, status, startDate, endDate, page, pageSize));
    }

    @Override
    public CommonResponse userDetail(String userId) {
        return CommonResponse.ok(userService.getUser(userId));
    }

    @Override
    public CommonResponse freezeUserOrNot(String userId, Integer doStatus) throws CustomizeException {
        if (!UserStatus.isUserStatusValid(doStatus)) {
            return CommonResponse.error(ErrorEnum.USER_STATUS_ERROR);
        }
        userService.freezeUserOrNot(userId, doStatus);
        // 删除用户会话，从而保障用户需要重新登录后再来刷新他的会话状态
        redis.del(REDIS_USER_INFO + ":" + userId);
        return CommonResponse.ok();
    }
}
