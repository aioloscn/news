package com.aiolos.news.controller.user.fallbacks;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务提供者熔断降级类，不写这个服务调用者会报500错误
 * @author Aiolos
 * @date 2020/12/11 8:51 下午
 */
@Slf4j
@Component
public class UserControllerFallbackFactory implements FallbackFactory<UserControllerApi> {

    @Override
    public UserControllerApi create(Throwable throwable) {
        return new UserControllerApi() {

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
            public CommonResponse getUserBasicInfo(String userId) {
                return getCause();
            }

            @Override
            public CommonResponse getAccountInfo(String userId) {
                return getCause();
            }

            @Override
            public CommonResponse updateAccountInfo(@Valid UpdateUserInfoBO updateUserInfoBO) throws CustomizedException {
                return getCause();
            }

            @Override
            public CommonResponse queryByIds(String userIds) {
                return getCause();
            }

            @Override
            public AppUser getUserByName(String nickname) {
                return null;
            }
        };
    }
}
