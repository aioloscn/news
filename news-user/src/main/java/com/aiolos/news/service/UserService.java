package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.pojo.AppUser;

/**
 * @author Aiolos
 * @date 2020/10/13 6:31 上午
 */
public interface UserService {

    /**
     * 判断用户是否存在，如果存在返回user信息
     * @param mobile
     * @return
     */
    AppUser queryMobileIsExist(String mobile);

    /**
     * 创建用户，新增用户记录到数据库
     * @param mobile
     * @return
     */
    AppUser creatUser(String mobile) throws CustomizeException;
}
