package com.aiolos.news.controller.files;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aiolos
 * @date 2020/11/7 11:46 上午
 */
@Api(value = "文件上传的Controller", tags = {"文件上传的Controller"})
@RequestMapping("/file")
public interface FileUploadControllerApi {

    /**
     * 上传单个文件
     * @param userId
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFace")
    CommonResponse uploadFace(@RequestParam String userId, MultipartFile file) throws Exception;

    /**
     * 上传多个文件
     * @param userId
     * @param files
     * @return
     */
    @PostMapping("/uploadSomeFiles")
    CommonResponse uploadSomeFiles(@RequestParam String userId, MultipartFile[] files) throws Exception;

    /**
     * 上传文件到MongoDB的GridFS中
     * @param newAdminBO
     * @return
     */
    @PostMapping("/uploadToGridFS")
    CommonResponse uploadToGridFS(@RequestBody NewAdminBO newAdminBO);
}
