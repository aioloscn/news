package com.aiolos.news.controller;

import com.aiolos.news.common.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;

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

    /**
     * 注入服务发现，可以获得已经注册的服务相关信息
     */
    @Autowired
    public DiscoveryClient discoveryClient;

    @Value("${website.domain-name}")
    public String DOMAIN_NAME;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final String REDIS_USER_INFO = "redis_user_info";

    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";

    public static final String REDIS_ALREADY_READ = "redis_already_read";

    public static final Integer COOKIE_EXPIRE_TIME = 7 * 24 * 60 * 60;

    public static final Integer START_PAGE = 1;

    public static final Integer PAGE_SIZE = 10;

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
        // 设置一级域名后面所有path都可以拿到这个cookie，比如aiolosxhx.com/login
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }

    public void deleteCookieValue(String cookieName, HttpServletRequest request, HttpServletResponse response) {

        setCookie(cookieName, "", 0, request, response);
    }

    public Integer getCountsFromRedis(String key) {

        String countsStr = redis.get(key);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }

        return Integer.valueOf(countsStr);
    }
}
