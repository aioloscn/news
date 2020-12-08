package com.aiolos.news.common.resources;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Aiolos
 * @date 2020/9/27 11:25 下午
 */
@Data
@Component
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "aliyun")
public class AliyunResource {

    private String accessKeyID;

    private String accessKeySecret;
}
