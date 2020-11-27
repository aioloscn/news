package com.aiolos.news.service.impl;

import com.aiolos.news.common.config.IdGeneratorSnowflake;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.dao.AdminUserDao;
import com.aiolos.news.pojo.AdminUser;
import com.aiolos.news.pojo.bo.NewAdminBO;
import com.aiolos.news.service.AdminUserService;
import com.aiolos.news.service.BaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/14 9:21 上午
 */
@Service
public class AdminUserServiceImpl extends BaseService implements AdminUserService {

    public final AdminUserDao adminUserDao;

    public final IdGeneratorSnowflake snowflake;

    public AdminUserServiceImpl(AdminUserDao adminUserDao, IdGeneratorSnowflake snowflake) {
        this.adminUserDao = adminUserDao;
        this.snowflake = snowflake;
    }

    @Override
    public AdminUser queryAdminByUsername(String username) {

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(username);
        QueryWrapper<AdminUser> wrapper = new QueryWrapper(adminUser);
        adminUser = adminUserDao.selectOne(wrapper);
        return adminUser;
    }

    @Transactional
    @Override
    public void createAdminUser(NewAdminBO newAdminBO) throws CustomizeException {

        AdminUser adminUser = new AdminUser();
        adminUser.setId(snowflake.nextIdStr());
        adminUser.setUsername(newAdminBO.getUsername());
        adminUser.setAdminName(newAdminBO.getAdminName());

        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {

            String pwd = BCrypt.hashpw(DigestUtils.md5Hex(newAdminBO.getPassword()), BCrypt.gensalt(12));
            adminUser.setPassword(pwd);
        }

        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());
        int resultCount = adminUserDao.insert(adminUser);

        if (resultCount != 1)
            throw new CustomizeException(ErrorEnum.ADMIN_INSERT_FAILED);
    }

    @Override
    public PagedResult queryAdminList(Integer page, Integer pageSize) {

        IPage<AdminUser> adminUserPage = new Page<>(page, pageSize);
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_time");
        adminUserPage = adminUserDao.selectPage(adminUserPage, wrapper);
        PagedResult pagedResult = setterPagedResult(adminUserPage);
        return pagedResult;
    }
}
