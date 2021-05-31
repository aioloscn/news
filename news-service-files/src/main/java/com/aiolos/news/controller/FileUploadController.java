package com.aiolos.news.controller;

import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.FileUtils;
import com.aiolos.news.pojo.bo.NewAdminBO;
import com.aiolos.news.resources.FileResource;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.controller.files.FileUploadControllerApi;
import com.aiolos.news.service.UploadService;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/7 12:07 下午
 */
@Slf4j
@RestController
public class FileUploadController implements FileUploadControllerApi {

    private final UploadService uploadService;

    private final FileResource fileResource;

    private final GridFSBucket gridFSBucket;

    public FileUploadController(UploadService uploadService, FileResource fileResource, GridFSBucket gridFSBucket) {
        this.uploadService = uploadService;
        this.fileResource = fileResource;
        this.gridFSBucket = gridFSBucket;
    }

    @Override
    public CommonResponse uploadFace(String userId, MultipartFile file) throws Exception {

        log.info("Enter the method uploadFace, parameters userId: {}", userId);

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
                path = uploadService.uploadFastDFS(file, suffix);
                // 上传到OSS
//                path = uploadService.uploadOSS(file, userId, suffix);
            } else {
                return CommonResponse.error(ErrorEnum.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return CommonResponse.error(ErrorEnum.FILE_UPLOAD_NULL_ERROR);
        }

        if (StringUtils.isNotBlank(path)) {

            // 返回到前端展示图片的路径
            path = fileResource.getHost() + path;
//            path = fileResource.getOssHost() + path;
        } else {
            return CommonResponse.error(ErrorEnum.FILE_UPLOAD_FAILED);
        }

        log.info("path: {}", path);
        return CommonResponse.ok((Object)path);
    }

    @Override
    public CommonResponse uploadSomeFiles(String userId, MultipartFile[] files) throws Exception {

        log.info("Enter the method uploadSomeFiles, parameter userId: {}", userId);

        // 声明list，用于存放多个图片路径，返回到前端
        List<String> imageUrlList = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {

                String path = "";
                if (file != null) {
                    // 获得文件上传的名称
                    String fileName = file.getOriginalFilename();
                    // 判断文件名不能为空
                    if (StringUtils.isNotBlank(fileName)) {

                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        // 判断后缀是否符合预定义规范
                        if (!suffix.equalsIgnoreCase("png") && !suffix.equalsIgnoreCase("jpg")
                                && !suffix.equalsIgnoreCase("jpeg")) {
                            continue;
                        }

                        // 执行上传
//                        path = uploadService.uploadOSS(file, userId, suffix);
                        path = uploadService.uploadFastDFS(file, suffix);
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }

                if (StringUtils.isNotBlank(path)) {
                    // TODO 对图片进行审核
//                    imageUrlList.add(fileResource.getOssHost() + path);
                    imageUrlList.add(fileResource.getHost() + path);
                } else {
                    continue;
                }
            }
        }
        return CommonResponse.ok(imageUrlList);
    }

    @Override
    public CommonResponse uploadToGridFS(NewAdminBO newAdminBO) {

        // 获得图片的base64字符串
        String img64 = newAdminBO.getImg64();
        String fileIdStr = StringUtils.EMPTY;
        try {
            // 将base64字符串转换为byte数组
            byte[] bytes = new BASE64Decoder().decodeBuffer(img64.trim());
            // 转换为输入流
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            // 上传到gridFS中
            ObjectId fileId = gridFSBucket.uploadFromStream(newAdminBO.getUsername() + ".png", byteArrayInputStream);
            // 获得文件在gridFS中的主键id
            fileIdStr = fileId.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResponse.ok("上传成功", fileIdStr);
    }

    @Override
    public void readInGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws CustomizedException {
        if (StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")) {
            throw new CustomizedException(ErrorEnum.FILE_DOES_NOT_EXIST_ERROR);
        }
        // 从GridFS中获取图片
        File adminFace = readGridFSByFaceId(faceId);
        // 把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(response, adminFace);
    }

    private File readGridFSByFaceId(String faceId) throws CustomizedException {
        GridFSFindIterable gridFSFiles = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));
        GridFSFile gridFSFile = gridFSFiles.first();
        if (gridFSFile == null) {
            throw new CustomizedException(ErrorEnum.FILE_DOES_NOT_EXIST_ERROR);
        }
        String filename = gridFSFile.getFilename();
        // 获取文件流，保存文件到本地或服务器临时目录
        File file = new File("/workspace/temp_face");
        if (!file.exists()) {
            file.mkdirs();
        }
        File myFile = new File("/workspace/temp_face" + filename);
        try {
            // 创建文件输出流
            OutputStream os = new FileOutputStream(myFile);
            // 下载到服务器或本地
            gridFSBucket.downloadToStream(new ObjectId(faceId), os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new CustomizedException(ErrorEnum.FILE_ACQUISITION_FAILED);
        }
        return myFile;
    }
}
