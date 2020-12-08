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

    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    public static final String REDIS_ALREADY_READ = "redis_already_read";

    public boolean verifyUserIdToken(String id, String token, String redisKeyPrefix) throws CustomizeException {

        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {

            System.out.println(redisKeyPrefix + ":" + id);
            String redisToken = redis.get(redisKeyPrefix + ":" + id);
            if (StringUtils.isBlank(redisToken) || !redisToken.equalsIgnoreCase(token)) {
                // token不一致，redis删除token，前端也会删除
                redis.del(REDIS_USER_TOKEN + ":" + id);
                redis.del(REDIS_USER_INFO + ":" + id);
                throw new CustomizeException(ErrorEnum.TOKEN_INVALID);
            }
        } else {
            throw new CustomizeException(ErrorEnum.USER_NOT_LOGGED_IN);
        }

        return true;
    }
}
