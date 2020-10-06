package com.aiolos.news;

import com.aiolos.news.common.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Aiolos
 * @date 2020/9/29 4:58 下午
 */
public class BaseController {

    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";
}
