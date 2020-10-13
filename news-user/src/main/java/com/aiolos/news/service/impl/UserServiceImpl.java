package com.aiolos.news.service.impl;

import com.aiolos.news.dao.AppUserDao;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Aiolos
 * @date 2020/10/13 6:46 上午
 */
@Service
public class UserServiceImpl implements UserService {

    private final AppUserDao appUserDao;

    public UserServiceImpl(AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    @Override
    public AppUser queryMobileIsExist(String mobile) {
        return null;
    }

    @Transactional
    @Override
    public AppUser creatUser(String mobile) {
        return null;
    }
}
