package com.aiolos.news.common.advice;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Aiolos
 * @date 2021/5/20 12:13 上午
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CommonResponse handlerException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
        log.info("全局异常捕获：{}", e.getMessage());
        if (e instanceof NoHandlerFoundException) {
            return CommonResponse.error(ErrorEnum.NO_HANDLER_FOUND);
        } else if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            return CommonResponse.error(ErrorEnum.PARAMETER_VALIDATION_ERROR.getErrCode(), CommonUtils.processErrorString(bindingResult));
        } else if (e instanceof ServletRequestBindingException) {
            return CommonResponse.error(ErrorEnum.BIND_EXCEPTION_ERROR);
        } else if (e instanceof NullPointerException) {
            return CommonResponse.error(ErrorEnum.NULL_POINT_ERROR);
        } else if (e instanceof MaxUploadSizeExceededException) {
            return CommonResponse.error(ErrorEnum.FILE_MAX_SIZE_ERROR);
        } else {
            return CommonResponse.error(ErrorEnum.UNKNOWN_ERROR);
        }
    }
}
