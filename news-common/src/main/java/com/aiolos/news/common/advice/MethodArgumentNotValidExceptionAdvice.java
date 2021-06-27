package com.aiolos.news.common.advice;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Aiolos
 * @date 2021/5/20 7:42 上午
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RestControllerAdvice
public class MethodArgumentNotValidExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CommonResponse handlerException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
        log.warn("请求参数校验不合格：{}", e.getMessage());
        BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        return CommonResponse.error(ErrorEnum.PARAMETER_VALIDATION_ERROR.getErrCode(), CommonUtils.processErrorString(bindingResult));
    }
}
