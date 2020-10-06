package com.aiolos.news.common;

/**
 *
 * 557：检查用户是否在CAS登录，用户门票的校验
 * @author Aiolos
 * @date 2020/9/22 2:50 下午
 */
public enum ErrorEnum implements CommonError {

    // 通用错误类型
    UNKNOWN_ERROR(10001, "未知错误"),
    BIND_EXCEPTION_ERROR(10002, "请求参数错误"),
    PARAMETER_VALIDATION_ERROR(10003, "请求参数校验失败"),
    HYSTRIX_FALLBACK(10004, "服务宕机或地址错误"),
    ROUTING_FAILURE(10005, "请求路由失败"),
    NO_HANDLER_FOUND(10006, "找不到执行的路径"),
    NULL_POINT_ERROR(10007, "缺少相关数据"),
    REDIS_ERROR(10008, "服务器缓存出现异常"),
    ES_SEARCH_ERROR(10009, "ES查询出错"),
    BEAN_ERROR(10010, "bean验证错误"),

    // 用户服务相关错误类型
    USER_NOT_LOGGED_IN(20000, "用户尚未登录"),
    REGISTER_DUP_FAIL(20001, "手机号已存在"),
    USER_DOES_NOT_EXIST(20002, "用户不存在"),
    LOGIN_FAIL(20003, "手机号或密码错误"),
    SEND_SMS_FAIL(20004, "发送验证码出现异常"),
    PHONE_INCORRECT(20005, "请输入正确的手机号"),
    SMS_CODE_EXPIRED(20006, "验证码已过期"),
    SMS_CODE_INCORRECT(20007, "验证码不正确"),
    FOLLOW_FAIL(20008, "关注失败"),
    CANCEL_FOLLOW_FAIL(20009, "取消关注失败"),
    CONSTRAINT_VIOLATION(20010, "违反唯一约束"),
    DUPLICATION_OPERATION(20011, "重复操作"),
    TOKEN_ERROR(20012, "会话失效，请重新登录"),
    VERIFY_QQ_ERROR(20013, "用户QQ校验异常"),


    // 业务相关错误类型
    INFORMATION_RELEASE_FAIL(30000, "短消息提交失败"),
    INFORMATION_IMAGE_RELEASE_FAIL(30001, "短消息的图片提交失败"),
    IMAGE_DATA_EMPTY(30002, "上传图片数据为空"),
    IMAGE_DATA_WRONGFUL(30003, "图片数据不合法"),
    DELETE_FAIL(30004, "删除失败"),
    THUMBS_UP_FAIL(30005, "点赞失败"),
    CANCEL_THUMBS_UP_FAIL(30006, "取消点赞失败"),
    RELEASE_FAIL(30007, "发布失败"),
    ACTIVITY_RELEASE_FAIL(30008, "活动发布失败"),
    ACTIVITY_IMAGE_RELEASE_FAIL(30009, "活动的图片提交失败"),
    ;

    ErrorEnum(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private Integer errCode;
    private String errMsg;

    @Override
    public Integer getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
