package com.aiolos.news.interceptors;

import com.aiolos.news.common.utils.IPUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Aiolos
 * @date 2020/12/8 4:25 下午
 */
public class ArticleReadInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String articleId = request.getParameter("articleId");
        String userIp = IPUtils.getRequestIp(request);

        // 设置针对当前用户ip的永久存在的key，存入到redis，表示该ip的用户已经阅读过了，无法累加阅读量
        boolean isExist = redis.keyIsExist(REDIS_ALREADY_READ + ":" + articleId + ":" + userIp);

        if (isExist) {
            return false;
        }
        return true;
    }
}
