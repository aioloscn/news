package com.aiolos.news.controller;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.controller.user.FansControllerApi;
import com.aiolos.news.service.FansService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aiolos
 * @date 2021/5/13 4:27 上午
 */
@Slf4j
@RestController
public class FansController extends BaseController implements FansControllerApi {

    private final FansService fansService;

    public FansController(FansService fansService) {
        this.fansService = fansService;
    }

    @Override
    public CommonResponse isMeFollowThisWriter(String writerId, String fanId) {
        if (StringUtils.isBlank(writerId)) {
            return CommonResponse.error(ErrorEnum.WRITER_ID_NULL_ERROR);
        }
        if (StringUtils.isBlank(fanId)) {
            return CommonResponse.error(ErrorEnum.FAN_ID_NULL_ERROR);
        }
        fansService.isMeFollowThisWriter(writerId, fanId);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse follow(String writerId, String fanId) throws CustomizeException {
        if (StringUtils.isBlank(writerId)) {
            return CommonResponse.error(ErrorEnum.WRITER_ID_NULL_ERROR);
        }
        if (StringUtils.isBlank(fanId)) {
            return CommonResponse.error(ErrorEnum.FAN_ID_NULL_ERROR);
        }
        fansService.follow(writerId, fanId);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse unfollow(String writerId, String fanId) throws CustomizeException {
        if (StringUtils.isBlank(writerId)) {
            return CommonResponse.error(ErrorEnum.WRITER_ID_NULL_ERROR);
        }
        if (StringUtils.isBlank(fanId)) {
            return CommonResponse.error(ErrorEnum.FAN_ID_NULL_ERROR);
        }
        fansService.unfollow(writerId, fanId);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse queryAll(String writerId, Integer page, Integer pageSize) {
        if (page == null) page = START_PAGE;
        if (pageSize == null) pageSize = PAGE_SIZE;
        return CommonResponse.ok(fansService.queryFansList(writerId, page, pageSize));
    }

    @Override
    public CommonResponse queryRatio(String writerId) {
        return CommonResponse.ok(fansService.queryFansCounts(writerId));
    }

    @Override
    public CommonResponse queryRatioByRegion(String writerId) {
        return CommonResponse.ok(fansService.queryRatioByRegion(writerId));
    }
}
