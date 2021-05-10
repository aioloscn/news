package com.aiolos.news.service.impl;

import com.aiolos.news.common.config.IdGeneratorSnowflake;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.enums.Sex;
import com.aiolos.news.common.enums.UserStatus;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.CommonUtils;
import com.aiolos.news.common.utils.DateUtils;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.RedisOperator;
import com.aiolos.news.dao.AppUserDao;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;
import com.aiolos.news.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/10/13 6:46 上午
 */
@Service
public class UserServiceImpl implements UserService {

    public static final String REDIS_USER_INFO = "redis_user_info";

    private final AppUserDao appUserDao;

    private final IdGeneratorSnowflake snowflake;

    private final RedisOperator redis;

    public UserServiceImpl(AppUserDao appUserDao, IdGeneratorSnowflake snowflake, RedisOperator redis) {
        this.appUserDao = appUserDao;
        this.snowflake = snowflake;
        this.redis = redis;
    }

    @Override
    public AppUser queryMobileIsExist(String mobile) {

        AppUser user = new AppUser();
        user.setMobile(mobile);
        QueryWrapper<AppUser> queryWrapper = new QueryWrapper(user);
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
        user.setSex(Sex.secret.getType());
        user.setActiveStatus(UserStatus.INACTIVE.getType());
        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        int resultCount = appUserDao.insert(user);
        if (resultCount != 1) {
            throw new CustomizeException(ErrorEnum.REGISTER_FAILED);
        }
        return user;
    }

    @Override
    public AppUser getUser(String userId) {
        return appUserDao.selectById(userId);
    }

    @Override
    public void updateAccountInfo(UpdateUserInfoBO updateUserInfoBO) throws CustomizeException {

        String userId = updateUserInfoBO.getId();

        // 保证双写一致，先删除redis中的数据，再更新数据库
        redis.del(REDIS_USER_INFO + ":" + userId);

        AppUser user = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO, user);
        user.setUpdatedTime(new Date());
        user.setActiveStatus(UserStatus.ACTIVE.getType());

        int affected = appUserDao.updateById(user);
        if (affected != 1) {
            throw new CustomizeException(ErrorEnum.USER_UPDATE_FAILED);
        }

        // 更新用户信息后，必须修改redis中保存的用户信息，因为删除redis信息到更新这段期间其他线程会获取到旧的值并set到redis
        AppUser appUser = getUser(userId);

        // 缓存双删策略
        try {
            Thread.sleep(100);
            redis.del(REDIS_USER_INFO + ":" + userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
