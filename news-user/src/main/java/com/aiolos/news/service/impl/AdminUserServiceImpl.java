package com.aiolos.news.service.impl;

import com.aiolos.news.dao.AdminUserDao;
import com.aiolos.news.service.AdminUserService;
import org.springframework.stereotype.Service;

/**
 * @author Aiolos
 * @date 2020/10/6 3:55 下午
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserDao adminUserDao;

    public AdminUserServiceImpl(AdminUserDao adminUserDao) {
        this.adminUserDao = adminUserDao;
    }

}
