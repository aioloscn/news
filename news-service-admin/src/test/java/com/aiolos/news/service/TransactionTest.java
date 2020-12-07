package com.aiolos.news.service;

import com.aiolos.news.TestApplication;
import com.aiolos.news.common.exception.CustomizeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Aiolos
 * @date 2020/12/4 6:31 上午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TransactionTest {

    @Autowired
    private SpringTransactionService springTransactionService;

    @Autowired
    private AnotherSpringTransaction anotherSpringTransaction;

    @Test
    public void testCatchExceptionCanNotRollback() {
        springTransactionService.catchExceptionCanNotRollback();
    }

    @Test
    public void testNotRuntimeExceptionCanNotRollback() throws CustomizeException {
        springTransactionService.notRuntimeExceptionCanNotRollback();
    }

    @Test
    public void testRuntimeExceptionCanRollback() {
        springTransactionService.runtimeExceptionCanRollback();
    }

    @Test
    public void testAssignExceptionCanRollback() throws CustomizeException {
        springTransactionService.assignExceptionCanRollback();
    }

    @Test
    public void testRollbackOnlyCanRollback() throws CustomizeException {
        springTransactionService.rollbackOnlyCanRollback();
    }

    @Test
    public void testNonTransactionalCanNotRollback() {
        springTransactionService.nonTransactionalCanNotRollback();
    }

    @Test
    public void testTransactionalCanRollback() {
        anotherSpringTransaction.transactionalCanRollback();
    }
}
