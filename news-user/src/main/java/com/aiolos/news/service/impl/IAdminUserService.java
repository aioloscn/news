package com.aiolos.news.service.impl;

import com.aiolos.news.dao.AdminUserDao;
import com.aiolos.news.pojo.AdminUser;
import com.aiolos.news.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/10/6 3:55 下午
 */
@Service
public class IAdminUserService implements AdminUserService {

    private final AdminUserDao adminUserDao;

    public IAdminUserService(AdminUserDao adminUserDao) {
        this.adminUserDao = adminUserDao;
    }

    @Override
    public void testJDBC() {
        List<AdminUser> result =  adminUserDao.selectAll();
    }
}
