package com.aiolos.news.service;

import com.aiolos.news.common.utils.PagedResult;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2021/5/9 1:30 上午
 */
public interface AppUserManagerService {

    /**
     * 查询管理员列表
     * @param nickname  昵称
     * @param status    状态
     * @param startDate 注册开始日期
     * @param endDate   注册结束日期
     * @param page      页数
     * @param pageSize  每页显示数
     * @return
     */
    PagedResult queryAllUserList(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize);
}
