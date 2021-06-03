package com.aiolos.news.zuul.filters;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.utils.JsonUtils;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aiolos
 * @date 2021/6/3 7:59 上午
 */
@Slf4j
@Component
public class MicroservicesErrorFilter extends SendErrorFilter {
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ExceptionHolder exception = findZuulException(ctx.getThrowable());
        log.error("{}服务未启动", ctx.get("serviceId"));
        HttpServletResponse resp = ctx.getResponse();
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("code", exception.getStatusCode());
            map.put("msg", ErrorEnum.ZUUL_FORWARDING_ERROR.getErrMsg());
            resp.getOutputStream().write(JsonUtils.objectToJson(map).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
