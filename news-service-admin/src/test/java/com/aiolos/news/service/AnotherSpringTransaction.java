package com.aiolos.news.service;

import com.aiolos.news.service.impl.SpringTransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aiolos
 * @date 2020/12/4 4:04 下午
 */
@Service
public class AnotherSpringTransaction {

    @Autowired
    private SpringTransactionServiceImpl springTransactionServiceImpl;

    /**
     * 在不同类中，一个不标注@Transactional的方法调用标注了@Transactional的方法，会发生事务回滚
     */
    public void transactionalCanRollback() {
        springTransactionServiceImpl.anotherOneSave();
    }
}
