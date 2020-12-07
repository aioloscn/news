package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizeException;

/**
 * Spring 事务测试接口定义
 * @author Aiolos
 * @date 2020/12/3 5:30 上午
 */
public interface SpringTransactionService {

    /**
     * 主动捕获异常，导致事务不能回滚
     */
    void catchExceptionCanNotRollback();

    /**
     * 抛出一个 checked 异常，导致事务不能回滚
     * 不是 unchecked 异常事务不能回滚
     * @throws CustomizeException   自定义异常类
     */
    void notRuntimeExceptionCanNotRollback() throws CustomizeException;

    /**
     * unchecked异常，事务可以回滚
     */
    void runtimeExceptionCanRollback();

    /**
     * 在rollbackFor里指定异常，Spring的事务处理可以回滚
     * @throws CustomizeException
     */
    void assignExceptionCanRollback() throws CustomizeException;

    /**
     * Rollback Only， 事务可以回滚
     * @throws CustomizeException
     */
    void rollbackOnlyCanRollback() throws CustomizeException;

    /**
     * 在同一个类中，一个不标注事务的方法去调用标注了事务的方法，事务会失效导致没有回滚
     */
    void nonTransactionalCanNotRollback();
}