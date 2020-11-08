package com.aiolos.news.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aiolos
 * @date 2020/11/6 5:00 下午
 */
public interface UploadService {

    String uploadFastDFS(MultipartFile file, String fileExtName) throws Exception;
}
