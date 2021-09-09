package com.aiolos.news.controller.article.fallbacks;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.controller.admin.CategoryMngControllerApi;
import com.aiolos.news.pojo.bo.SaveCategoryBO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aiolos
 * @date 2021/9/9 6:49 上午
 */
@Slf4j
@Component
public class CategoryMngControllerFallbackFactory implements FallbackFactory<CategoryMngControllerApi> {
    @Override
    public CategoryMngControllerApi create(Throwable throwable) {
        return new CategoryMngControllerApi() {

            private CommonResponse getCause() {
                log.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION + ": " + throwable.getMessage());
                Pattern pattern = Pattern.compile("(?<=\"msg\":\")(.*?)(?=\")");
                Matcher matcher = pattern.matcher(throwable.getMessage());
                String cause = StringUtils.EMPTY;
                if (matcher.find()) {
                    cause = matcher.group(0);
                }
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
            }

            @Override
            public CommonResponse getCatList() {
                return getCause();
            }

            @Override
            public CommonResponse saveOrUpdateCategory(@Valid SaveCategoryBO saveCategoryBO) throws CustomizedException {
                return getCause();
            }
        };
    }
}
