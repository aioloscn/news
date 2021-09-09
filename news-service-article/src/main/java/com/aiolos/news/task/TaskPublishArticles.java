package com.aiolos.news.task;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.service.ArticleService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Aiolos
 * @date 2020/11/27 12:28 上午
 */
@Configuration
@EnableScheduling
public class TaskPublishArticles {

    public final ArticleService articleService;

    public TaskPublishArticles(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Scheduled(cron = "0 */5 * * * ? ")
    private void publishArticles() throws CustomizedException {

        // 将爬虫项目保存在ES的新闻数据在本系统发布
        articleService.publishNewsFromESData();
    }
}
