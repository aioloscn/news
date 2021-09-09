package com.aiolos.news.pojo.eo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Aiolos
 * @date 2021/9/8 10:56 下午
 */
@Data
public class Payload {

    @JsonProperty("Category")
    private String category;

    @JsonProperty("Pubtime")
    private String pubtime;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Content")
    private String content;
}
