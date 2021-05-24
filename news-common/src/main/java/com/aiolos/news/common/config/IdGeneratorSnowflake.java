package com.aiolos.news.common.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Aiolos
 * @date 2020/10/14 10:26 上午
 */
@Slf4j
@Component
public class IdGeneratorSnowflake {
    private long workerId = 0;
    private long datacenterId = 1;
    private Snowflake snowflake = IdUtil.createSnowflake(workerId, datacenterId);

    @PostConstruct
    public void init() {
        try {
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            System.out.println("当前机器的workerId: " + workerId);
        } catch (Exception e) {
            System.out.println("当前机器的workerId获取失败" + e);
            workerId = NetUtil.getLocalhostStr().hashCode();
            System.out.println("当前机器 workId:" + workerId);
        }

    }

    public synchronized long nextId() {
        return snowflake.nextId();
    }

    public synchronized String nextIdStr() {
        return snowflake.nextIdStr();
    }

    public synchronized long nextId(long workerId, long datacenterId) {
        snowflake = IdUtil.createSnowflake(workerId, datacenterId);
        return snowflake.nextId();
    }

    public static void main(String[] args) {

        System.out.println(new IdGeneratorSnowflake().nextId());
    }


}
