package com.aiolos.news.controller;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.CommonUtils;
import com.aiolos.news.controller.admin.FriendLinkControllerApi;
import com.aiolos.news.pojo.bo.SaveFriendLinkBO;
import com.aiolos.news.pojo.mo.FriendLinkMO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/11/27 6:28 下午
 */
@Slf4j
@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

    @Override
    public CommonResponse saveOrUpdateFriendLink(@Valid SaveFriendLinkBO saveFriendLinkBO, BindingResult bindingResult) throws CustomizeException {

        if (bindingResult.hasErrors())
            throw new CustomizeException(ErrorEnum.PARAMETER_VALIDATION_ERROR, CommonUtils.processErrorString(bindingResult));

        FriendLinkMO friendLinkMO = new FriendLinkMO();
        BeanUtils.copyProperties(saveFriendLinkBO, friendLinkMO);
        friendLinkMO.setCreateTime(new Date());
        friendLinkMO.setUpdateTime(new Date());

        return CommonResponse.ok();
    }
}
