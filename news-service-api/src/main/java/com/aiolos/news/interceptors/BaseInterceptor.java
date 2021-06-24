package com.aiolos.news.interceptors;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.exception.TokenInvalidException;
import com.aiolos.news.common.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Aiolos
 * @date 2020/10/30 3:40 下午
 */
public class BaseInterceptor {

    @Resource
    public RedisOperator redis;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final String REDIS_USER_INFO = "redis_user_info";

    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    public static final String REDIS_ALREADY_READ = "redis_already_read";

    /**
     * 验证用户token
     * @param id
     * @param token
     * @param redisKeyPrefix
     * @return
     * @throws CustomizedException
     */
    public boolean verifyUserIdToken(String id, String token, String redisKeyPrefix) throws CustomizedException {

        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {

            System.out.println(redisKeyPrefix + ":" + id);
            String redisToken = redis.get(redisKeyPrefix + ":" + id);
            if (StringUtils.isBlank(redisToken) || !redisToken.equalsIgnoreCase(token)) {
                // token不一致，redis删除token，前端也会删除
                redis.del(REDIS_USER_TOKEN + ":" + id);
                redis.del(REDIS_USER_INFO + ":" + id);
                throw new TokenInvalidException(ErrorEnum.TOKEN_INVALID);
            }
        } else {
            throw new TokenInvalidException(ErrorEnum.USER_NOT_LOGGED_IN);
        }

        return true;
    }

    /**
     * 从cookie中取值
     * @param request
     * @param key
     * @return
     */
    public String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                String value = cookie.getValue();
                return value;
            }
        }
        return null;
    }
}
