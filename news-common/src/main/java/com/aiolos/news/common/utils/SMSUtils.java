package com.aiolos.news.common.utils;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.exception.ErrorEnum;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Aiolos
 * @date 2020/9/28 8:20 下午
 */
@Slf4j
@Component
public class SMSUtils {

    @Autowired
    public AliyunResource aliyunResource;

    public void sendSMS(String mobile, String code) throws CustomizeException {

        log.info("Enter function sendSMS, parameter mobile: {}, code: {}", mobile, code);

        if (StringUtils.isBlank(mobile))
            throw new CustomizeException(ErrorEnum.PHONE_INCORRECT);

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                aliyunResource.getAccessKeyID(),
                aliyunResource.getAccessKeySecret());
//        DefaultProfile.addEndpoint("cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");   // 另一种写法
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
//        SendSmsRequest request = new SendSmsRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");

        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "i校易点");
        request.putQueryParameter("TemplateCode", "SMS_178465190");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

//        request.setPhoneNumbers(mobile);
//        request.setSignName("i校易点");
//        request.setTemplateCode("SMS_178465190");
//        request.setTemplateParam("{\"code\": \""+ code +"\"}");
//        request.setOutId("yourOutId");

        try {
            CommonResponse response = client.getCommonResponse(request);
//            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
