package com.aiolos.news.service.impl;

import com.aiolos.news.service.UploadService;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aiolos
 * @date 2020/11/6 5:01 下午
 */
@Service
public class UploadServiceImpl implements UploadService {

    public final FastFileStorageClient fastFileStorageClient;

    public UploadServiceImpl(FastFileStorageClient fastFileStorageClient) {
        this.fastFileStorageClient = fastFileStorageClient;
    }

    @Override
    public String uploadFastDFS(MultipartFile file, String fileExtName) throws Exception {

        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        return storePath.getFullPath();
    }
}
