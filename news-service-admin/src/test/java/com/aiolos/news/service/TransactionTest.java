package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizedException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Aiolos
 * @date 2020/12/4 6:31 上午
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TransactionTest {

    @Autowired
    private SpringTransactionService springTransactionService;

    @Autowired
    private AnotherSpringTransaction anotherSpringTransaction;

//    @Test
    public void testCatchExceptionCanNotRollback() {
        springTransactionService.catchExceptionCanNotRollback();
    }

//    @Test
    public void testNotRuntimeExceptionCanNotRollback() throws CustomizedException {
        springTransactionService.notRuntimeExceptionCanNotRollback();
    }

//    @Test
    public void testRuntimeExceptionCanRollback() {
        springTransactionService.runtimeExceptionCanRollback();
    }

//    @Test
    public void testAssignExceptionCanRollback() throws CustomizedException {
        springTransactionService.assignExceptionCanRollback();
    }

//    @Test
    public void testRollbackOnlyCanRollback() throws CustomizedException {
        springTransactionService.rollbackOnlyCanRollback();
    }

//    @Test
    public void testNonTransactionalCanNotRollback() {
        springTransactionService.nonTransactionalCanNotRollback();
    }

//    @Test
    public void testTransactionalCanRollback() {
        anotherSpringTransaction.transactionalCanRollback();
    }
}
