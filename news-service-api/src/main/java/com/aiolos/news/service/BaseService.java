package com.aiolos.news.service;

import com.aiolos.news.common.config.IdGeneratorSnowflake;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.common.utils.RedisOperator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * @author Aiolos
 * @date 2020/11/26 6:16 上午
 */
public class BaseService {

    @Autowired
    public RedisOperator redis;

    @Autowired
    public IdGeneratorSnowflake idWorker;

    /**
     * 注入服务发现，可以获得已经注册的服务相关信息
     */
    @Autowired
    public DiscoveryClient discoveryClient;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";
    public static final long REDIS_ALL_CATEGORY_TIME_OUT = 24 * 60 * 60;

    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";

    public static final String REDIS_WRITER_FANS_COUNT = "redis_writer_fans_count";

    public static final String REDIS_MY_FOLLOW_COUNT = "redis_my_follow_count";

    public static final String ARTICLE_READ_COUNTS_ZSET = "article_read_counts_zset";

    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";

    public static final String ES_NEW_ID = "es_new_id";

    public PagedResult setterPagedResult(IPage<?> page) {

        PagedResult pagedResult = new PagedResult();
        pagedResult.setCurrent(page.getCurrent());
        pagedResult.setPages(page.getPages());
        pagedResult.setTotal(page.getTotal());
        pagedResult.setRecords(page.getRecords());
        return pagedResult;
    }

    public Integer getCountsFromRedis(String key) {

        String countsStr = redis.get(key);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }

        return Integer.valueOf(countsStr);
    }
}
