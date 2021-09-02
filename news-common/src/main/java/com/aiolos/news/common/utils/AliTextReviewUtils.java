package com.aiolos.news.common.utils;

import com.aiolos.news.common.resources.AliyunResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Aiolos
 * @date 2021/5/15 4:39 上午
 */
@Slf4j
@Component
public class AliTextReviewUtils {

    @Autowired
    private AliyunResource aliyunResource;

    public String reviewTextContent(String content) {
        IClientProfile profile = DefaultProfile
                .getProfile("cn-shanghai", aliyunResource.getAccessKeyID(), aliyunResource.getAccessKeySecret());
        try {
            DefaultProfile
                    .addEndpoint("cn-shanghai", "cn-shanghai", "Green", "green.cn-shanghai.aliyuncs.com");
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient client = new DefaultAcsClient(profile);
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // 指定API返回格式。
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法。
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId("cn-shanghai");
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * 待检测的文本，长度不超过10000个字符。
         */
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /**
         * 检测场景。文本垃圾检测请传递antispam。
         **/
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        log.info(JSON.toJSONString(data, true));
        try {
            textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 请务必设置超时时间。
        textScanRequest.setConnectTimeout(3000);
        textScanRequest.setReadTimeout(6000);
        try {
            HttpResponse httpResponse = client.doAction(textScanRequest);
            if(httpResponse.isSuccess()){
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                log.info(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if(200 == ((JSONObject)taskResult).getInteger("code")){
                            JSONArray sceneResults = ((JSONObject)taskResult).getJSONArray("results");
                            JSONObject sceneResult = (JSONObject) sceneResults.get(0);
//                            for (Object sceneResult : sceneResults) {
                                String scene = ((JSONObject)sceneResult).getString("scene");
                                String suggestion = ((JSONObject)sceneResult).getString("suggestion");
                                // 根据scene和suggetion做相关处理。
                                // suggestion == pass表示未命中垃圾。suggestion == block表示命中了垃圾，可以通过label字段查看命中的垃圾分类。
                                log.info("args = [" + scene + "]");
                                log.info("args = [" + suggestion + "]");

                                // pass: 文本正常，文章状态改为发布通过
                                // review: 需要人工审核，需要在后台管理进行人工审核，大部分平台都采用机审+人工审核的方式
                                // block: 文本违规，可以直接删除或者做限制处理，审核不通过
                                return suggestion;
//                            }
                        }else{
                            log.error("task process fail:" + ((JSONObject)taskResult).getInteger("code"));
                        }
                    }
                } else {
                    log.error("detect not success. code:" + scrResponse.getInteger("code"));
                }
            }else{
                log.error("response not success. status:" + httpResponse.getStatus());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
