package com.aiolos.news.controller.user.fallbacks;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.controller.user.UserControllerApi;
import com.aiolos.news.pojo.bo.UpdateUserInfoBO;
import com.aiolos.news.pojo.vo.UserBasicInfoVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
            @Override
            public CommonResponse getUserBasicInfo(String userId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse getAccountInfo(String userId) {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse updateAccountInfo(@Valid UpdateUserInfoBO updateUserInfoBO) throws CustomizeException {
                log.error("Connection refused, enter the degraded method of the service caller");
                return CommonResponse.error(ErrorEnum.FEIGN_FALLBACK_EXCEPTION);
            }

            @Override
            public CommonResponse queryByIds(String userIds) {

                log.error("Connection refused, enter the degraded method of the service caller");
                List<UserBasicInfoVO> publisherList = new ArrayList<>();
                return CommonResponse.ok(publisherList);
            }
        };
    }
}
