package com.aiolos.news.pojo.validate;

import com.aiolos.news.common.utils.UrlUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Aiolos
 * @date 2020/11/27 6:16 下午
 */
public class CheckUrlValidate implements ConstraintValidator<CheckUrl, String> {

    @Override
    public boolean isValid(String url, ConstraintValidatorContext constraintValidatorContext) {
        return UrlUtils.verifyUrl(url.trim());
    }
}
