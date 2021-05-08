package com.aiolos.news.controller;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.controller.user.AppUserManagerControllerApi;
import com.aiolos.news.service.AppUserManagerService;
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

    public AppUserManagerController(AppUserManagerService appUserManagerService) {
        this.appUserManagerService = appUserManagerService;
    }

    @Override
    public CommonResponse queryAll(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        if (page == null) page = START_PAGE;
        if (pageSize == null) pageSize = PAGE_SIZE;
        return CommonResponse.ok(appUserManagerService.queryAllUserList(nickname, status, startDate, endDate, page, pageSize));
    }
}
