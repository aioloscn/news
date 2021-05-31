package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;

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
    AppUser creatUser(String mobile) throws CustomizedException;

    /**
     * 根据用户主键ID查询用户信息
     * @param userId
     * @return
     */
    AppUser getUser(String userId);

    /**
     * 修改用户信息完善资料，并且激活
     * @param updateUserInfoBO
     * @return
     */
    void updateAccountInfo(UpdateUserInfoBO updateUserInfoBO) throws CustomizedException;

    void freezeUserOrNot(String userId, Integer doStatus) throws CustomizedException;
}
