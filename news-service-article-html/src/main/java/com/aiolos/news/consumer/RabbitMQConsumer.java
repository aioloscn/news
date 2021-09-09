package com.aiolos.news.consumer;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.component.ArticleHtmlComponent;
import com.aiolos.news.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConversionException;
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
    public void watchDownloadQueue(String payload, Message message) {
        log.info("rabbitmq consumer watchDownloadQueue param: {}", payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.download")) {
            String articleId = payload.split(",")[0];
            String articleMongoId = payload.split(",")[1];
            // 从GridFS下载静态文章资源到前端项目
            Integer status = articleHtmlComponent.download(articleId, articleMongoId);
            if (status != HttpStatus.OK.value()) {
                log.error("下载静态html{}失败，丢弃消息", articleId);
                throw new MessageConversionException(ErrorEnum.ARTICLE_REVIEW_ERROR.getErrMsg());
            }
        }
    }

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DELETE_HTML})
    public void watchDeleteQueue(String payload, Message message) {
        log.info("rabbitmq consumer watchDeleteQueue param: {}", payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.delete")) {
            // 删除前端项目中的指定的静态文章
            Integer status = articleHtmlComponent.delete(payload);
            if (status != HttpStatus.OK.value()) {
                log.error("删除静态html{}失败，丢弃消息", payload);
                throw new MessageConversionException(ErrorEnum.FAILED_TO_DELETE_ARTICLE.getErrMsg());
            }
        }
    }
}
