package com.aiolos.news.common.advice;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.enums.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 所有执行的controller都会被这个切面所包含
 * @author Aiolos
 * @date 2020/10/10 4:14 下午
 */
@Slf4j
@RestControllerAdvice
public class CustomizeExceptionAdvice {

    /**
     * 定义ExceptionHandler解决为被controller层吸收的Exception和它的子类异常
     * 默认配置中，Spring事务框架只会将Runtime、unchecked异常的事务标记为回滚
     * 但是如果捕获RuntimeException的异常，Spring校验会用DefaultHandlerExceptionResolver解析器，从而进不了该类
     * 如果捕获Exception异常，Spring校验会用ExceptionHandlerExceptionResolver解析器
     * @return  返回封装好的公共web对象
     */
    @ExceptionHandler(value = CustomizeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CommonResponse handlerCustomizeException(HttpServletRequest req, HttpServletResponse res, Exception e) {

        if (e instanceof CustomizeException) {
            log.info("自定义异常捕获，异常信息：{}", ((CustomizeException) e).getErrMsg());
            return CommonResponse.error(((CustomizeException) e).getErrCode(), ((CustomizeException) e).getErrMsg());
        } else {
            return CommonResponse.error(ErrorEnum.UNKNOWN_ERROR);
        }
    }
}