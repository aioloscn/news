package com.aiolos.news.pojo.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description
 * @Author aiolos
 * @Date 2022/2/16 16:22
 * @Version 1.0
 **/
@Data
public class WooCommerceShopAuthBO {
    
    @JsonProperty("consumer_key")
    private String consumerKey;

    @JsonProperty("consumer_secret")
    private String consumerSecret;

    @JsonProperty("key_id")
    private String keyId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("key_permissions")
    private String keyPermissions;

    private String shop;
}
