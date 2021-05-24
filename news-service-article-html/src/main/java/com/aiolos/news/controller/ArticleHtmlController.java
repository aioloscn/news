package com.aiolos.news.controller;

import com.aiolos.news.controller.article.ArticleHtmlControllerApi;
import com.mongodb.client.gridfs.GridFSBucket;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Aiolos
 * @date 2021/5/18 3:50 上午
 */
@Api(value = "用于其他模块远程调用的文章静态页下载删除controller", tags = "引入MQ后不再使用这种方式")
@Slf4j
@RestController
public class ArticleHtmlController implements ArticleHtmlControllerApi {

    @Value("${freemarker.html.article}")
    private String articlePath;

    private final GridFSBucket gridFSBucket;

    public ArticleHtmlController(GridFSBucket gridFSBucket) {
        this.gridFSBucket = gridFSBucket;
    }

    @Override
    public Integer download(String articleId, String articleMongoId) {
        try {
            File file = new File(articlePath + File.separator + articleId + ".html");
            OutputStream outputStream = new FileOutputStream(file);
            gridFSBucket.downloadToStream(new ObjectId(articleMongoId), outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        return HttpStatus.OK.value();
    }

    @Override
    public Integer delete(String articleId) {
        File file = new File(articlePath + File.separator + articleId + ".html");
        boolean deleted = file.delete();
        return deleted ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
