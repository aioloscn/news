package com.aiolos.news.interceptors;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.enums.UserStatus;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.pojo.AppUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户激活状态检查拦截器
 * 发表/修改/删除文章、发表/查看评论等等这些接口都是需要在用户激活以后才能进行
 * 否则需要提示用户前往[账号设置]去修改信息
 * @author Aiolos
 * @date 2020/10/31 1:26 上午
 */
public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {

    /**
     * 拦截请求，在访问controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("headerUserId");
        String userJson = redis.get(REDIS_USER_INFO + ":" + userId);
        AppUser user = null;
        if (StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        }

        if (user.getActiveStatus() == null || !user.getActiveStatus().equals(UserStatus.ACTIVE.type)) {
            throw new CustomizeException(ErrorEnum.USER_INACTIVE_ERROR);
        }
        return true;
    }

    /**
     * 请求访问到controller之后，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问到controller之后，渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
