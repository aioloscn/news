package com.aiolos.news.controller;

import com.aiolos.news.common.response.CommonResponse;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.controller.admin.FriendLinkControllerApi;
import com.aiolos.news.pojo.bo.SaveFriendLinkBO;
import com.aiolos.news.pojo.bo.WooCommerceShopAuthBO;
import com.aiolos.news.pojo.mo.FriendLinkMO;
import com.aiolos.news.service.FriendLinkService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.Enumeration;

/**
 * @author Aiolos
 * @date 2020/11/27 6:28 下午
 */
@Slf4j
@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

    private final FriendLinkService friendLinkService;

    public FriendLinkController(FriendLinkService friendLinkService) {
        this.friendLinkService = friendLinkService;
    }

    @Override
    public CommonResponse saveOrUpdateFriendLink(@Valid SaveFriendLinkBO saveFriendLinkBO) throws CustomizedException {

        log.info("Enter the method saveOrUpdateFriendLink, parameter saveFriendLinkBO: {}", saveFriendLinkBO.toString());

        FriendLinkMO friendLinkMO = new FriendLinkMO();
        BeanUtils.copyProperties(saveFriendLinkBO, friendLinkMO);
        friendLinkMO.setCreateTime(new Date());
        friendLinkMO.setUpdateTime(new Date());

        friendLinkService.saveOrUpdateFriendLink(friendLinkMO);

        return CommonResponse.ok();
    }

    @Override
    public CommonResponse getFriendLinkList() {

        log.info("Enter the method getFriendLinkList");
        return CommonResponse.ok(friendLinkService.queryAllFriendLinks());
    }

    @Override
    public CommonResponse delete(String linkId) {

        log.info("Enter the method admin/friendLinkMng/delete, parameter linkId: {}", linkId);

        friendLinkService.delete(linkId);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse queryPortalAllFriendLinkList() {

        log.info("Enter the method admin/friendLinkMng/portal/list");
        return CommonResponse.ok(friendLinkService.queryPortalAllFriendLinkList());
    }

    @Override
    public void productsReceive(JSONObject data, HttpServletRequest request) {
        log.info("woocommerce data: {}", data);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            log.info("{}: {}", key, value);
        }
    }

    @Override
    public void callback(WooCommerceShopAuthBO shopAuthBO, HttpServletRequest request) {
        log.info("woocommerce data: {}", shopAuthBO);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            log.info("{}: {}", key, value);
        }
    }
}
