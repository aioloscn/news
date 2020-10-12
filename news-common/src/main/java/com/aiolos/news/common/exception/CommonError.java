package com.aiolos.news.common.exception;

/**
 * @author Aiolos
 * @date 2020/9/22 2:51 下午
 */
public interface CommonError {

    Integer getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
