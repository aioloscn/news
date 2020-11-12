package com.aiolos.news.service.impl;

import com.aiolos.news.common.config.IdGeneratorSnowflake;
import com.aiolos.news.common.utils.AliyunResource;
import com.aiolos.news.resources.FileResource;
import com.aiolos.news.service.UploadService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * @author Aiolos
 * @date 2020/11/6 5:01 下午
 */
@Service
public class UploadServiceImpl implements UploadService {

    public final FastFileStorageClient fastFileStorageClient;

    public final FileResource fileResource;

    public final AliyunResource aliyunResource;

    public final IdGeneratorSnowflake snowflake;

    public UploadServiceImpl(FastFileStorageClient fastFileStorageClient, FileResource fileResource, AliyunResource aliyunResource, IdGeneratorSnowflake snowflake) {
        this.fastFileStorageClient = fastFileStorageClient;
        this.fileResource = fileResource;
        this.aliyunResource = aliyunResource;
        this.snowflake = snowflake;
    }

    @Override
    public String uploadFastDFS(MultipartFile file, String fileExtName) throws Exception {

        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        return storePath.getFullPath();
    }

    @Override
    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception {

        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = fileResource.getEndpoint();
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = aliyunResource.getAccessKeyID();
        String accessKeySecret = aliyunResource.getAccessKeySecret();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String myObjectName = fileResource.getObjectName() + File.separator + userId + File.separator + snowflake.nextIdStr() + "." + fileExtName;

        // 上传网络流。
        InputStream inputStream = file.getInputStream();
        ossClient.putObject(fileResource.getBucketName(), myObjectName, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        return myObjectName;
    }
}
