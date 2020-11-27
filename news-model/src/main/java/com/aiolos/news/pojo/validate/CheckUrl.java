package com.aiolos.news.pojo.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Aiolos
 * @date 2020/11/27 6:13 下午
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckUrlValidate.class)
public @interface CheckUrl {

    String message() default "Url不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
