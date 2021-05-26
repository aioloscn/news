package com.aiolos.news.consumer;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.config.RabbitMQDelayQueueConfig;
import com.aiolos.news.pojo.Article;
import com.aiolos.news.pojo.eo.ArticleEO;
import com.aiolos.news.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

/**
 * 延迟队列消费者，负责定时发布文章
 * @author Aiolos
 * @date 2021/5/20 11:00 下午
 */
@Slf4j
@Component
public class RabbitMQDelayConsumer {

    private final ArticleService articleService;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public RabbitMQDelayConsumer(ArticleService articleService, ElasticsearchTemplate elasticsearchTemplate) {
        this.articleService = articleService;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @RabbitListener(queues = {RabbitMQDelayQueueConfig.DELAY_QUEUE})
    public void watchDelayQueue(String payload, Message message) {
        String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("rabbitmq consumer watchDelayQueue payload: {}, routingKey: {}", payload, receivedRoutingKey);
        String articleId = payload;
        // 消费者接收到定时发布的延迟消息，修改当前文章状态为即时发布
        if (StringUtils.isBlank(articleId) || articleId.equalsIgnoreCase("null")) {
            log.warn("延时消息丢失");
            return;
        }
        try {
            articleService.updateArticleToPublish(articleId);
        } catch (CustomizeException e) {
            log.error(articleId + e.getErrMsg());
        }

        Article article = articleService.queryById(articleId);
        ArticleEO articleEO = new ArticleEO();
        BeanUtils.copyProperties(article, articleEO);
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(articleEO).build();
        String index = elasticsearchTemplate.index(indexQuery);
        log.info("修改文章{}发布状态为即时发布，保存ES索引: {}", articleId, index);
        if (StringUtils.isBlank(index)) {
            log.error("修改文章{}发布状态为即时发布，保存ES索引失败", articleId);
        }
    }
}
