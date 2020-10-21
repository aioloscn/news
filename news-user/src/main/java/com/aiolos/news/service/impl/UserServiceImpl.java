package com.aiolos.news.service.impl;

import com.aiolos.news.common.config.IdGeneratorSnowflake;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.enums.Sex;
import com.aiolos.news.common.enums.UserStatus;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.CommonUtils;
import com.aiolos.news.common.utils.DateUtils;
import com.aiolos.news.dao.AppUserDao;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/10/13 6:46 上午
 */
@Service
public class UserServiceImpl implements UserService {

    private final AppUserDao appUserDao;

    private final IdGeneratorSnowflake snowflake;

    public UserServiceImpl(AppUserDao appUserDao, IdGeneratorSnowflake snowflake) {
        this.appUserDao = appUserDao;
        this.snowflake = snowflake;
    }

    @Override
    public AppUser queryMobileIsExist(String mobile) {

        AppUser user = new AppUser();
        user.setMobile(mobile);
        QueryWrapper queryWrapper = new QueryWrapper(user);
        user = appUserDao.selectOne(queryWrapper);

        return user;
    }

    @Transactional
    @Override
    public AppUser creatUser(String mobile) throws CustomizeException {

        /**
         * 互联网项目都要考虑可扩展性
         * 如果未来的业务激增，那么就需要分库分表
         * 所以数据库主键ID必须保证全局唯一
         */
        AppUser user = new AppUser();
        user.setId(snowflake.nextIdStr());
        user.setMobile(mobile);
        user.setNickname("用户" + CommonUtils.hidePhoneNo(mobile));
        user.setFace("");
        user.setBirthday(DateUtils.strToDate("1970-01-01 00:00:00"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);
        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        int resultCount = appUserDao.insert(user);
        if (resultCount != 1)
            throw new CustomizeException(ErrorEnum.REGISTER_FAIL);
        return user;
    }
}
