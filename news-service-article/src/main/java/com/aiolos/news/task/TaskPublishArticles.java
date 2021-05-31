package com.aiolos.news.task;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.service.ArticleService;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Aiolos
 * @date 2020/11/27 12:28 上午
 */
//@Configuration
//@EnableScheduling
public class TaskPublishArticles {

    public final ArticleService articleService;

    public TaskPublishArticles(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Scheduled(cron = "0/3 * * * * ? ")
    private void publishArticles() throws CustomizedException {

        // 把当前时间应该发布的定时文章，状态改为即时
        articleService.updateAppointToPublish();
    }
}
