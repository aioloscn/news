package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.pojo.AdminUser;
import com.aiolos.news.pojo.bo.NewAdminBO;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/14 9:19 上午
 */
public interface AdminUserService {

    /**
     * 根据用户名查询管理员信息
     * @param username  用户名
     * @return
     */
    AdminUser queryAdminByUsername(String username);

    /**
     * 新增管理员
     * @param newAdminBO
     * @return
     */
    void createAdminUser(NewAdminBO newAdminBO) throws CustomizeException;

    /**
     * 分页查询admin列表
     * @param page
     * @param pageSize
     */
    PagedResult queryAdminList(Integer page, Integer pageSize);
}
