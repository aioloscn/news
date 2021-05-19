package com.aiolos.news.component;

import com.mongodb.client.gridfs.GridFSBucket;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Aiolos
 * @date 2021/5/19 9:15 下午
 */
@Component
public class ArticleHtmlComponent {

    @Value("${freemarker.html.article}")
    private String articlePath;

    private final GridFSBucket gridFSBucket;

    public ArticleHtmlComponent(GridFSBucket gridFSBucket) {
        this.gridFSBucket = gridFSBucket;
    }

    public Integer download(String articleId, String articleMongoId) {
        if (StringUtils.isBlank(articleId) || articleId.equalsIgnoreCase("null")
                || StringUtils.isBlank(articleMongoId) || articleMongoId.equalsIgnoreCase("null"))
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
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

    public Integer delete(String articleId) {
        if (StringUtils.isBlank(articleId) || articleId.equalsIgnoreCase("null"))
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        File file = new File(articlePath + File.separator + articleId + ".html");
        boolean deleted = file.delete();
        return deleted ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
