package com.aiolos.news.common.advice;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.utils.CommonUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Aiolos
 * @date 2021/4/17 9:52 下午
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CommonResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return CommonResponse.error(ErrorEnum.PARAMETER_VALIDATION_ERROR.getErrCode(), CommonUtils.processErrorString(bindingResult));
    }
}
