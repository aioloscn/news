package com.aiolos.news.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aiolos
 * @date 2020/11/6 5:00 下午
 */
public interface UploadService {

    /**
     * 使用FastDFS上传文件
     * @param file          文件
     * @param fileExtName   后缀名
     * @return
     * @throws Exception
     */
    String uploadFastDFS(MultipartFile file, String fileExtName) throws Exception;

    /**
     * 使用OSS上传文件
     * @param file          文件
     * @param userId        用户ID
     * @param fileExtName   后缀名
     * @return
     * @throws Exception
     */
    String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception;
}
