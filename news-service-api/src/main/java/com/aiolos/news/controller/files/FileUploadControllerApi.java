package com.aiolos.news.controller.files;

import com.aiolos.news.common.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
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

    @ApiOperation(value = "上传用户头像", notes = "上传用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    CommonResponse uploadFace(@RequestParam String userId, MultipartFile file) throws Exception;
}
