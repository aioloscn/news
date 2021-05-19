package com.aiolos.news.utils;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.JsonUtils;
import com.aiolos.news.config.RabbitMQConfig;
import com.aiolos.news.controller.article.ArticleHtmlControllerApi;
import com.aiolos.news.controller.article.ArticlePortalControllerApi;
import com.aiolos.news.pojo.vo.ArticleDetailVO;
import com.mongodb.client.gridfs.GridFSBucket;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aiolos
 * @date 2021/5/17 5:55 下午
 */
@Component
public class ArticleUtil {

    @Value("${freemarker.html.article}")
    private String articlePath;

    private final ArticlePortalControllerApi articlePortalControllerApi;

    private final GridFSBucket gridFSBucket;

    private final ArticleHtmlControllerApi articleHtmlControllerApi;

    private final RabbitTemplate rabbitTemplate;

    public ArticleUtil(ArticlePortalControllerApi articlePortalControllerApi, GridFSBucket gridFSBucket, ArticleHtmlControllerApi articleHtmlControllerApi, RabbitTemplate rabbitTemplate) {
        this.articlePortalControllerApi = articlePortalControllerApi;
        this.gridFSBucket = gridFSBucket;
        this.articleHtmlControllerApi = articleHtmlControllerApi;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 生成静态文章HTML
     * @param articleId
     */
    public void createArticleHtml(String articleId) {
        // 0. 配置freemarker基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        // 声明freemarker所需要加载的目录的位置
        String classPath = this.getClass().getResource("/").getPath();
        try {
            cfg.setDirectoryForTemplateLoading(new File(classPath + "templates"));
            Template template = cfg.getTemplate("detail.ftl", "utf-8");

            // 获得文章详情数据
            ArticleDetailVO articleDetailVO = getArticleDetail(articleId);
            Map<String, Object> map = new HashMap<>();
            map.put("articleDetail", articleDetailVO);

            File tempDic = new File(articlePath);
            if (!tempDic.exists()) {
                tempDic.mkdirs();
            }

            Writer out = new FileWriter(articlePath + File.separator + articleId + ".html");
            template.process(map, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成静态文章HTML并上传到GridFS
     * @param articleId
     */
    public String createArticleHtmlToGridFS(String articleId) {
        // 0. 配置freemarker基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        // 声明freemarker所需要加载的目录的位置
        String classPath = this.getClass().getResource("/").getPath();
        try {
            cfg.setDirectoryForTemplateLoading(new File(classPath + "templates"));
            Template template = cfg.getTemplate("detail.ftl", "utf-8");

            // 获得文章详情数据
            ArticleDetailVO articleDetailVO = getArticleDetail(articleId);
            Map<String, Object> map = new HashMap<>();
            map.put("articleDetail", articleDetailVO);

            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(htmlContent);
            ObjectId fileId = gridFSBucket.uploadFromStream(articleId + ".html", inputStream);
            return fileId.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文章主键获取文章详情数据
     * @param articleId
     * @return
     */
    public ArticleDetailVO getArticleDetail(String articleId) {
        CommonResponse resp = articlePortalControllerApi.detail(articleId);
        ArticleDetailVO articleDetailVO = null;
        if (resp != null && resp.getCode() == HttpStatus.OK.value()) {
            String articleJson = JsonUtils.objectToJson(resp.getData());
            articleDetailVO = JsonUtils.jsonToPojo(articleJson, ArticleDetailVO.class);
        }
        return articleDetailVO;
    }

    /**
     * 从mongodb GridFS中下载静态html
     * @param articleId 文章主键
     * @param articleMongoId    MongoDB文章文件的主键
     * @throws CustomizeException
     */
    public void downloadArticleHtml(String articleId, String articleMongoId) throws CustomizeException {
        Integer status = articleHtmlControllerApi.download(articleId, articleMongoId);
        if (status != HttpStatus.OK.value()) {
            throw new CustomizeException(ErrorEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    /**
     * 发送消息到MQ队列，让消费端监听并执行后续的下载
     * @param articleId 文章主键
     * @param articleMongoId    MongoDB文章文件的主键
     */
    public void downloadArticleHtmlByMQ(String articleId, String articleMongoId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE, "article.download", articleId + "," + articleMongoId);
    }

    /**
     * 删除mongodb GridFS中的文章关联数据
     * @param articleMongoId
     */
    public void deleteFromGridFS(String articleMongoId) {
        if (StringUtils.isBlank(articleMongoId)) return;
        gridFSBucket.delete(new ObjectId(articleMongoId));
    }

    /**
     * 删除服务器或本地的静态文章html
     * @param articleId
     * @throws CustomizeException
     */
    public void deleteArticleHtml(String articleId) throws CustomizeException {
        Integer status = articleHtmlControllerApi.delete(articleId);
        if (status != HttpStatus.OK.value()) {
            throw new CustomizeException(ErrorEnum.FAILED_TO_DELETE_ARTICLE);
        }
    }

    /**
     * 发送消息到mq队列，让消费端监听并删除html
     * @param articleId
     */
    public void deleteArticleHtmlByMQ(String articleId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE, "article.delete", articleId);
    }
}
