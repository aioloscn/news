package com.aiolos.news.pojo.eo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author Aiolos
 * @date 2021/9/8 10:50 下午
 */
@Document(indexName = "dating_news", type = "_doc")
@Data
public class DatingNewsEO {

    @Id
    private String id;

    @JsonProperty("Payload")
    private Payload payload;
}
