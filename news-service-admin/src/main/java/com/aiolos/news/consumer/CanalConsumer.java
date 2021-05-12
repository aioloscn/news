package com.aiolos.news.consumer;

import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.mo.CanalBean;
import com.aiolos.news.service.BaseService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Aiolos
 * @date 2021/5/12 2:38 上午
 */
@Slf4j
@Component
public class CanalConsumer extends BaseService {

    @KafkaListener(topics = "canaltopic")
    public void receive(ConsumerRecord<?, ?> consumer) {
        String value = (String) consumer.value();
        log.info("topic名称:{}, key:{}, 分区位置:{}, 下标:{}, value:{}", consumer.topic(), consumer.key(), consumer.partition(), consumer.offset(), value);
        CanalBean canalBean = JSONObject.parseObject(value, CanalBean.class);
        boolean isDdl = canalBean.isDdl();
        String type = canalBean.getType();
        if (!isDdl) {
            List<Category> categories = canalBean.getData();
            // 过期时间
            if ("INSERT".equals(type)) {
                for (Category category : categories) {
                    Integer id = category.getId();
                    redis.set(REDIS_ALL_CATEGORY + ":" + id, JsonUtils.objectToJson(category), REDIS_ALL_CATEGORY_TIME_OUT);
                }
            } else if ("UPDATE".equals(type)) {
                for (Category category : categories) {
                    Integer id = category.getId();
                    redis.set(REDIS_ALL_CATEGORY + ":" + id, JsonUtils.objectToJson(category), REDIS_ALL_CATEGORY_TIME_OUT);
                }
            } else {
                for (Category category : categories) {
                    Integer id = category.getId();
                    redis.del(REDIS_ALL_CATEGORY + ":" +id);
                }
            }
        }
    }
}
