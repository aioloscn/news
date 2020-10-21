package com.aiolos.news;

import com.aiolos.news.common.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Aiolos
 * @date 2020/9/29 4:58 下午
 */
public class BaseController {

    @Autowired
    public RedisOperator redis;

    @Value("${website.domain-name}")
    public String DOMAIN_NAME;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final Integer COOKIE_EXPIRE_TIME = 7 * 24 * 60 * 60;

    public void setCookie(String cookieName, String cookieValue, Integer expireTime,
                          HttpServletRequest request, HttpServletResponse response) {

        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            setCookieValue(cookieName, cookieValue, expireTime, request, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setCookieValue(String cookieName, String cookieValue, Integer expireTime,
                               HttpServletRequest request, HttpServletResponse response) {

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(expireTime);
        // 只要是在这个域名下都可以拿到这个cookie
        cookie.setDomain(DOMAIN_NAME);
        // 设置域名后面所有path都可以拿到这个cookie，比如aiolosxhx.com/login
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
