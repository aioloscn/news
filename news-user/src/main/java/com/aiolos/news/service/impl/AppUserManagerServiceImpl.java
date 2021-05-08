package com.aiolos.news.service.impl;

import com.aiolos.news.common.enums.UserStatus;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.dao.AppUserDao;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.service.AppUserManagerService;
import com.aiolos.news.service.BaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2021/5/9 1:33 上午
 */
@Service
public class AppUserManagerServiceImpl extends BaseService implements AppUserManagerService {

    private final AppUserDao appUserDao;

    public AppUserManagerServiceImpl(AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    @Override
    public PagedResult queryAllUserList(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        IPage<AppUser> appUserIPage = new Page<>(page, pageSize);
        QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_time");
        if (StringUtils.isNotBlank(nickname)) {
            queryWrapper.like("nickname", nickname);
        }
        if (UserStatus.isUserStatusValid(status)) {
            queryWrapper.eq("active_status", status);
        }
        if (startDate != null) {
            queryWrapper.ge("created_time", startDate);
        }
        if (endDate != null) {
            queryWrapper.le("created_time", endDate);
        }
        appUserIPage = appUserDao.selectPage(appUserIPage, queryWrapper);
        PagedResult pagedResult = setterPagedResult(appUserIPage);
        return pagedResult;
    }
}
