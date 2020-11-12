package com.aiolos.news.interceptors;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Aiolos
 * @date 2020/10/30 3:40 下午
 */
public class BaseInterceptor {

    @Autowired
    public RedisOperator redis;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final String REDIS_USER_INFO = "redis_user_info";

    public boolean verifyUserIdToken(String id, String token, String redisKeyPrefix) throws CustomizeException {

        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {

            String redisToken = redis.get(redisKeyPrefix + ":" + id);
            if (StringUtils.isBlank(redisToken) || !redisToken.equalsIgnoreCase(token)) {
                throw new CustomizeException(ErrorEnum.TOKEN_INVALID);
            }
        } else {
            throw new CustomizeException(ErrorEnum.USER_NOT_LOGGED_IN);
        }

        return true;
    }
}