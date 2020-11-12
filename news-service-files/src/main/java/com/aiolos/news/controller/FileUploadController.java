package com.aiolos.news.controller;

import com.aiolos.news.resources.FileResource;
import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.controller.files.FileUploadControllerApi;
import com.aiolos.news.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aiolos
 * @date 2020/11/7 12:07 下午
 */
@Slf4j
@RestController
public class FileUploadController implements FileUploadControllerApi {

    private final UploadService uploadService;

    private final FileResource fileResource;

    public FileUploadController(UploadService uploadService, FileResource fileResource) {
        this.uploadService = uploadService;
        this.fileResource = fileResource;
    }

    @Override
    public CommonResponse uploadFace(String userId, MultipartFile file) throws Exception {

        log.info("Enter function uploadFace, parameters userId: {}", userId);

        String path = null;

        if (file != null) {

            String fileName = file.getOriginalFilename();
            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {

                String fileNameArr[] = fileName.split("\\.");
                // 获得后缀
                String suffix = fileNameArr[fileNameArr.length - 1];
                // 判断后缀是否符合我们定义的规范
                if (!suffix.equalsIgnoreCase("png") && !suffix.equalsIgnoreCase("jpg")
                        && !suffix.equalsIgnoreCase("jpeg")) {
                    return CommonResponse.error(ErrorEnum.FILE_FORMAT_ERROR);
                }

                // 上传到FastDFS
//                path = uploadService.uploadFastDFS(file, suffix);
                // 上传到OSS
                path = uploadService.uploadOSS(file, userId, suffix);
            } else {
                return CommonResponse.error(ErrorEnum.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return CommonResponse.error(ErrorEnum.FILE_UPLOAD_NULL_ERROR);
        }

        if (StringUtils.isNotBlank(path))
//            path = fileResource.getHost() + path;
            path = fileResource.getOssHost() + path;
        else
            return CommonResponse.error(ErrorEnum.FILE_UPLOAD_FAIL);

        log.info("path: {}", path);
        return CommonResponse.ok((Object)path);
    }
}
