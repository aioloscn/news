package com.aiolos.news.controller;

import com.aiolos.news.common.CommonResponse;
import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.CommonUtils;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.common.utils.RedisOperator;
import com.aiolos.news.controller.admin.AdminControllerApi;
import com.aiolos.news.pojo.AdminUser;
import com.aiolos.news.pojo.bo.AdminLoginBO;
import com.aiolos.news.pojo.bo.NewAdminBO;
import com.aiolos.news.service.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Aiolos
 * @date 2020/11/14 1:03 下午
 */
@Slf4j
@RestController
public class AdminController extends BaseController implements AdminControllerApi {

    public final AdminUserService adminUserService;

    public final RedisOperator redis;

    public AdminController(AdminUserService adminUserService, RedisOperator redis) {
        this.adminUserService = adminUserService;
        this.redis = redis;
    }

    @Override
    public CommonResponse adminLogin(AdminLoginBO adminLoginBO, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws CustomizeException {

        log.info("Enter function adminLogin, parameter adminLoginBO: {}", adminLoginBO.toString());

        if (bindingResult.hasErrors())
            throw new CustomizeException(ErrorEnum.PARAMETER_VALIDATION_ERROR, CommonUtils.processErrorString(bindingResult));

        AdminUser adminUser = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());

        if (adminUser == null)
            throw new CustomizeException(ErrorEnum.ADMIN_NOT_EXIST_ERROR);

        if (BCrypt.checkpw(DigestUtils.md5Hex(adminLoginBO.getPassword()), adminUser.getPassword())) {

            doLoginSettings(adminUser, request, response);
            return CommonResponse.ok();
        } else {
            return CommonResponse.error(ErrorEnum.ADMIN_NOT_EXIST_ERROR);
        }

    }

    @Override
    public CommonResponse adminIsExist(String username) throws CustomizeException {

        log.info("Enter function adminIsExist, parameter username: {}", username);

        checkAdminExist(username);
        return CommonResponse.ok();
    }

    @Override
    public CommonResponse addNewAdmin(NewAdminBO newAdminBO) throws CustomizeException {

        log.info("Enter function addNewAdmin, parameter newAdminBO: {}", newAdminBO);

        // base64不为空，则代表人脸入库，否则需要用户输入密码和确认密码
        if (StringUtils.isBlank(newAdminBO.getImg64())) {

            if (StringUtils.isBlank(newAdminBO.getPassword())) {

                if (StringUtils.isBlank(newAdminBO.getConfirmPassword())) {
                    return CommonResponse.error(ErrorEnum.ADMIN_PASSWORD_NULL_ERROR);
                }
            }
        }

        // 如果密码不为空，判断两次输入是否一致
        if (StringUtils.isBlank(newAdminBO.getPassword()) || !newAdminBO.getPassword().equals(newAdminBO.getConfirmPassword())) {
            return CommonResponse.error(ErrorEnum.ADMIN_PASSWORD_ERROR);
        }

        // 校验用户名唯一
        checkAdminExist(newAdminBO.getUsername());
        adminUserService.createAdminUser(newAdminBO);

        return CommonResponse.ok();
    }

    @Override
    public CommonResponse getAdminList(Integer pageNum, Integer pageSize) {

        log.info("Enter function getAdminList, parameter page: {}, pageSize: {}", pageNum, pageSize);

        if (pageNum == null)
            pageNum = START_PAGE;
        if (pageSize == null)
            pageSize = PAGE_SIZE;

        PagedResult pagedResult = adminUserService.queryAdminList(pageNum, pageSize);
        return CommonResponse.ok(pagedResult);
    }

    @Override
    public CommonResponse adminLogout(String adminId, HttpServletRequest request, HttpServletResponse response) {

        log.info("Enter function adminLogout, parameter adminId: {}", adminId);

        // 从redis删除admin会话token
        redis.del(REDIS_ADMIN_TOKEN + ":" + adminId);
        deleteCookieValue("aid", request, response);
        deleteCookieValue("aname", request, response);
        deleteCookieValue("atoken", request, response);
        return CommonResponse.ok();
    }

    private void doLoginSettings(AdminUser adminUser, HttpServletRequest request, HttpServletResponse response) {

        // 保存token到redis中
        String token = UUID.randomUUID().toString();
        redis.set(REDIS_ADMIN_TOKEN + ":" + adminUser.getId(), token);

        setCookie("aid", adminUser.getId(), COOKIE_EXPIRE_TIME, request, response);
        setCookie("aname", adminUser.getAdminName(), COOKIE_EXPIRE_TIME, request, response);
        setCookie("atoken", token, COOKIE_EXPIRE_TIME, request, response);
    }

    private void checkAdminExist(String username) throws CustomizeException {

        AdminUser adminUser = adminUserService.queryAdminByUsername(username);

        if (adminUser != null)
            throw new CustomizeException(ErrorEnum.ADMIN_USERNAME_EXIST_ERROR);
    }
}
