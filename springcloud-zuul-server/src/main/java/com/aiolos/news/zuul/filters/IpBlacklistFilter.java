package com.aiolos.news.zuul.filters;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.utils.IPUtils;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.RedisOperator;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Aiolos
 * @date 2020/12/12 10:00 下午
 */
@Slf4j
@Component
@RefreshScope
public class IpBlacklistFilter extends ZuulFilter {

    private final RedisOperator redis ;

    @Value("${ipBlacklist.continueCounts}")
    private Integer continueCounts;

    @Value("${ipBlacklist.timeInterval}")
    private Integer timeInterval;

    @Value("${ipBlacklist.limitTime}")
    private Integer limitTime;

    public IpBlacklistFilter(RedisOperator redis) {
        this.redis = redis;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        System.out.println("continueCounts: " + continueCounts);
        System.out.println("timeInterval: " + timeInterval);
        System.out.println("limitTime: " + limitTime);

        log.info("Enter ip blacklist filter");

        // 获得上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        // 获得ip
        String ip = IPUtils.getRequestIp(request);

        /**
         * 判断ip在10秒内的请求次数是否超过10次
         * 如果超过，则限制这个ip访问15秒，15秒后再放行
         */
        final String ipRedisKey = "zuul-ip:" + ip;
        final String ipLimitRedisKey = "zuul-ip-limit:" + ip;

        // 获得当前ip这个key的剩余时间
        long remainingTime = redis.ttl(ipLimitRedisKey);
        // 如果当前限制ip的key还存在剩余时间，说明这个ip不能访问，继续等待
        if (remainingTime > 0) {
            stopRequest(context);
            return null;
        }

        // 在redis中累加ip的请求次数
        long requestCounts = redis.increment(ipRedisKey, 1);
        // 从0开始计数，第一次访问为1，则设置连续请求的间隔时间
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }

        // 如果还能取得请求次数，说明用户连续请求的次数在10秒内
        // 一旦请求次数超过了连续访问的次数，则需要限制这个ip的访问
        if (requestCounts > continueCounts) {
            // 限制ip的访问时间，value随便填
            redis.set(ipLimitRedisKey, ipLimitRedisKey, limitTime);
            stopRequest(context);
        }

        return null;
    }

    private void stopRequest(RequestContext context) {

        // 停止zuul继续向下路由，禁止请求通信
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(200);
        String responseBody = JsonUtils.objectToJson(CommonResponse.error(ErrorEnum.SYSTEM_ZUUL_ERROR));
        context.setResponseBody(responseBody);
        context.getResponse().setCharacterEncoding("utf-8");
        context.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
