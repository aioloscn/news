package com.aiolos.news.common.exception;

/**
 * 自定义异常类
 * 由于 CustomizeException 继承自 Exception类，所以它是 checked 异常
 * 如果程序中抛出 CustomizeException 异常，在默认情况下，Spring 的事务处理是不会回滚的
 * @author Aiolos
 * @date 2020/10/7 10:03 下午
 */
public class CustomizeException extends Exception implements CommonError {

    private final CommonError commonError;

    public CustomizeException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }

    /**
     * 因为ErrorEnum实现了CommonError，所以在抛出异常的时候可以传CommonError的实现类ErrorEnum进来，在ErrorEnum中setErrMsg()
     * @param commonError   公共的异常接口
     * @param errMsg    错误信息
     */
    public CustomizeException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public Integer getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    /**
     * 在CommonError的被实现类（ErrorEnum）中替换message
     * @param errMsg    错误信息
     * @return  返回CommonError的实现类，也就是当前类
     */
    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
