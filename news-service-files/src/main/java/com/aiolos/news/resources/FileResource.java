package com.aiolos.news.resources;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Aiolos
 * @date 2020/11/9 4:02 上午
 */
@Getter
@Setter
@Component
@PropertySource("classpath:file-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "file")
public class FileResource {

    private String host;

    private String ossHost;

    private String endpoint;

    private String bucketName;

    private String objectName;
}
