package com.aiolos.news.service.impl;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.dao.AdminUserDao;
import com.aiolos.news.pojo.AdminUser;
import com.aiolos.news.service.SpringTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;

/**
 * @author Aiolos
 * @date 2020/12/3 7:53 下午
 */
@Service
public class SpringTransactionServiceImpl implements SpringTransactionService {

    private final AdminUserDao adminUserDao;

    public SpringTransactionServiceImpl(AdminUserDao adminUserDao) {
        this.adminUserDao = adminUserDao;
    }

    @Override
    @Transactional
    public void catchExceptionCanNotRollback() {

        try {
            AdminUser user = new AdminUser();
            user.setUsername("qqq");
            user.setCreatedTime(new Date());
            user.setUpdatedTime(new Date());
            adminUserDao.insert(user);
            throw new RuntimeException();
        } catch (Exception ex) {
            ex.printStackTrace();
            // 主动捕获异常想回滚的话，可以手动标记回滚
            // 把当前的TransactionStatus的状态设置为rollbackOnly，让它标记为回滚
            // 这种方法不友好，会把Spring事务的处理代码侵入到自己的代码中，会与Spring代码形成一种耦合
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

    }

    @Override
    @Transactional
    public void notRuntimeExceptionCanNotRollback() throws CustomizedException {

        try {
            AdminUser user = new AdminUser();
            user.setUsername("aaa");
            user.setCreatedTime(new Date());
            user.setUpdatedTime(new Date());
            adminUserDao.insert(user);
            throw new RuntimeException();
        } catch (Exception ex) {
            // 抛出的自定义异常不是unchecked所以不会rollback
            throw new CustomizedException(ErrorEnum.HYSTRIX_FALLBACK, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void runtimeExceptionCanRollback() {

        AdminUser user = new AdminUser();
        user.setUsername("canRollback");
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        adminUserDao.insert(user);
        throw new RuntimeException();
    }

    @Override
    @Transactional(rollbackFor = {CustomizedException.class})
    public void assignExceptionCanRollback() throws CustomizedException {

        try {
            AdminUser user = new AdminUser();
            user.setUsername("xxx");
            user.setCreatedTime(new Date());
            user.setUpdatedTime(new Date());
            adminUserDao.insert(user);
            throw new RuntimeException();
        } catch (Exception ex) {
            // 将RuntimeException改为自定义异常
            throw new CustomizedException(ErrorEnum.HYSTRIX_FALLBACK, ex.getMessage());
        }
    }

    /**
     * @Transactional注解不作用在public上不起作用
     */
    @Transactional
    public void oneSave() {

        AdminUser user = new AdminUser();
        user.setUsername("oneSave");
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        adminUserDao.insert(user);
    }

    /**
     * 在事务方法中，调用多个事务方法的时候，Spring会把这些事务合而为一
     * 当整个方法中的每一个子方法都没有报错，整个方法执行完时才会进行事务提交
     * 如果某个方法出现了异常，Spring就会将异常标记为rollbackOnly
     * @throws CustomizedException
     */
    @Override
    public void rollbackOnlyCanRollback() throws CustomizedException {

        oneSave();

        try {
            // 字符不能为空，insert操作会报错

            AdminUser user = new AdminUser();
            adminUserDao.insert(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            // 如果这里没有try catch捕获这个异常，最终导致事务处理失败，而不是事务回滚
            // 最好是向外抛出异常，让Spring感受到异常的存在，才能过正确的进行事务回滚
             throw ex;
        }
    }

    @Transactional
    public void anotherOneSave() {

        AdminUser user = new AdminUser();
        user.setUsername("anotherOneSave");
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        adminUserDao.insert(user);
        // unchecked异常，事务可以回滚
        throw new RuntimeException();
    }

    /**
     * 这个方法不标注@Transactional，去调用有标注@Transactional的方法，事务会失效导致没有回滚，划重点-同一个类中
     */
    @Override
    public void nonTransactionalCanNotRollback() {
        anotherOneSave();
    }
}
