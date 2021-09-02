package com.aiolos.news.consumer;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.component.ArticleHtmlComponent;
import com.aiolos.news.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * rabbitmq消费端，负责下载和删除静态文章html
 * @author Aiolos
 * @date 2021/5/19 7:53 下午
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    private final ArticleHtmlComponent articleHtmlComponent;

    public RabbitMQConsumer(ArticleHtmlComponent articleHtmlComponent) {
        this.articleHtmlComponent = articleHtmlComponent;
    }

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DOWNLOAD_HTML})
    public void watchDownloadQueue(String payload, Message message) throws CustomizedException {
        log.info("rabbitmq consumer watchDownloadQueue param: {}", payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.download")) {
            String articleId = payload.split(",")[0];
            String articleMongoId = payload.split(",")[1];
            // 从GridFS下载静态文章资源到前端项目
            Integer status = articleHtmlComponent.download(articleId, articleMongoId);
            if (status != HttpStatus.OK.value()) {
                throw new CustomizedException(ErrorEnum.ARTICLE_REVIEW_ERROR);
            }
        }
    }

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DELETE_HTML})
    public void watchDeleteQueue(String payload, Message message) throws CustomizedException {
        log.info("rabbitmq consumer watchDeleteQueue param: {}", payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.delete")) {
            // 删除前端项目中的指定的静态文章
            Integer status = articleHtmlComponent.delete(payload);
            if (status != HttpStatus.OK.value()) {
                throw new CustomizedException(ErrorEnum.FAILED_TO_DELETE_ARTICLE);
            }
        }
    }
}
