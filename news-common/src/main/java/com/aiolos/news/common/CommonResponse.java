package com.aiolos.news.common;

import java.io.Serializable;

/**
 * 自定义响应数据结构
 * 状态码定义在ErrorEnum类中，200：成功（默认），500：错误（默认）
 * @author Aiolos
 * @date 2020/9/22 1:11 下午
 */
public class CommonResponse<T> implements Serializable {

    // 响应业务状态码
    private Integer code;

    // 响应信息
    private String msg;

    // 响应中的数据
    private T data;

    private CommonResponse() {

    }

    private CommonResponse(String msg) {
        this.code = 200;
        this.msg = msg;
    }

    private CommonResponse(T data) {
        this.code = 200;
        this.msg = "OK";
        this.data = data;
    }

    private CommonResponse(String msg, T data) {
        this.code = 200;
        this.msg = msg;
        this.data = data;
    }

    public static CommonResponse ok(String msg) {
        return new CommonResponse(msg);
    }

    public static CommonResponse ok(Object data) {
        return new CommonResponse(data);
    }

    public static CommonResponse ok(String msg, Object data) {
        return new CommonResponse(msg, data);
    }

    public static CommonResponse error(String msg) {
        CommonResponse res = new CommonResponse();
        res.code = 500;
        res.msg = msg;
        return res;
    }

    public static CommonResponse error(String msg, Object data) {
        CommonResponse res = new CommonResponse();
        res.code = 500;
        res.msg = msg;
        res.data = data;
        return res;
    }

    public static CommonResponse error(Integer code, String msg, Object data) {
        CommonResponse res = new CommonResponse();
        res.code = code;
        res.msg = msg;
        res.data = data;
        return res;
    }
}
