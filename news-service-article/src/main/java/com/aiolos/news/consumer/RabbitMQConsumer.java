package com.aiolos.news.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.common.utils.RedisOperator;
import com.aiolos.news.config.RabbitMQConfig;
import com.aiolos.news.pojo.Category;
import com.aiolos.news.pojo.bo.NewArticleAndCategoryBO;
import com.aiolos.news.pojo.bo.NewArticleBO;
import com.aiolos.news.service.ArticleService;
import com.aiolos.news.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

/**
 * @author Aiolos
 * @date 2021/9/9 6:57 上午
 */
@Slf4j
@Component
public class RabbitMQConsumer extends BaseService {

    private final ArticleService articleService;

    public RabbitMQConsumer(ArticleService articleService) {
        this.articleService = articleService;
    }

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_INSERT_ARTICLE})
    public void watchInsertQueue(String payload, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.insert")) {
            NewArticleAndCategoryBO bo = JsonUtils.jsonToPojo(payload, NewArticleAndCategoryBO.class);
            NewArticleBO newArticleBO = new NewArticleBO();
            BeanUtil.copyProperties(bo, newArticleBO);
            Category category = new Category();
            BeanUtil.copyProperties(bo, category);
            log.info("rabbitmq接收到定时任务数据，category id: {}", category.getId());
            if (newArticleBO == null || category == null) {
                log.error("从ES同步新闻到数据库失败，对象为空，丢弃消息");
                throw new MessageConversionException(ErrorEnum.ARTICLE_CREATE_FAILED.getErrMsg());
            } else {
                log.info("rabbitmq consumer watchInsertQueue article title: {}", newArticleBO.getTitle());
                try {
                    articleService.createArticle(newArticleBO, category);
                    // 保存Id用于新闻去重
                    redis.set(ES_NEW_ID + ":" + bo.getNewId(), bo.getNewId());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new MessageConversionException(ErrorEnum.ARTICLE_CREATE_FAILED.getErrMsg());
                }
            }
        }
    }
}
