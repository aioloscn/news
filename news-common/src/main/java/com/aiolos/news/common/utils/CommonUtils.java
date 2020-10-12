package com.aiolos.news.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * @author Aiolos
 * @date 2020/10/11 11:23 下午
 */
public class CommonUtils {

    /**
     * 将数组中的错误信息拼接成字符串
     * @param bindingResult
     * @return
     */
    public static String processErrorString(BindingResult bindingResult) {

        if (!bindingResult.hasErrors())
            return StringUtils.EMPTY;

        StringBuilder sb = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {

            sb.append(error.getDefaultMessage() + ",");
        }
        return sb.substring(0, sb.length() - 1);
    }
}