package com.aiolos.news.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Aiolos
 * @date 2020/10/14 10:56 下午
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    //    用户状态：
    //    0：未激活。
    //    1：已激活：基本信息是否完善，真实姓名，邮箱地址，性别，生日，住址等，
    //              如果没有完善，则用户不能发表评论，不能点赞，不能关注，不能操作任何入库的功能。
    //    2：已冻结。


    INACTIVE(0, "未激活"),
    ACTIVE(1, "已激活"),
    FROZEN(2, "已冻结");

    private final Integer type;
    private final String value;

    /**
     * 判断传入的用户状态是不是有效的值
     * @param tempStatus
     * @return
     */
    public static boolean isUserStatusValid(Integer tempStatus) {
        if (tempStatus != null) {
            if (tempStatus.equals(INACTIVE.type) || tempStatus.equals(ACTIVE.type) || tempStatus.equals(FROZEN.type)) {
                return true;
            }
        }
        return false;
    }
}