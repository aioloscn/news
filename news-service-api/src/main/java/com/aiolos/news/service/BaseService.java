package com.aiolos.news.service;

import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.common.utils.RedisOperator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Aiolos
 * @date 2020/11/26 6:16 上午
 */
public class BaseService {

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    @Autowired
    public RedisOperator redis;

    public PagedResult setterPagedResult(IPage<?> page) {

        PagedResult pagedResult = new PagedResult();
        pagedResult.setCurrent(page.getCurrent());
        pagedResult.setPages(page.getPages());
        pagedResult.setTotal(page.getTotal());
        pagedResult.setRecords(page.getRecords());
        return pagedResult;
    }
}
