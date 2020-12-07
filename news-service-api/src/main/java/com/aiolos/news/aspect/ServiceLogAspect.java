package com.aiolos.news.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP通知
 * 1.前置通知（@Before）：在某连接点（join point）之前执行执行的通知，但这个通知不能阻止连接点前的执行（除非抛出异常）
 * 2.后置通知（@After）：在调用Service方法之后，不论是正常返回还是异常退出
 * 3.环绕通知（@Around）：在调用Service方法之前和之后
 * 4.异常通知（@AfterThrowing）：Service方法执行过程中抛出异常退出时执行的通知
 * 5.返回通知（@AfterReturning）：在某连接点（join point）正常完成后执行的通知
 * @author Aiolos
 * @date 2020/10/31 2:14 上午
 */
@Slf4j
@Aspect
@Component
public class ServiceLogAspect {

    /**
     * 记录service执行的时间
     * 第一个*：代表拦截到的对应方法的返回类型，*指所有类型
     * 第二个*：代表拦截所有功能模块的service
     * 两个..：代表当前包以及子包下所有的类，这里是拦截impl下面的类以及它的子包下的类
     * 第三个*：代表当前路径下所有的类
     * 第四个*：代表当前类的所有方法
     * (..)：代表扩号里可以是任意参数，可以有参可以无参
     */
    @Around("execution(* com.aiolos.*.service.impl..*.*(..))")
    public Object recordTimeOfService(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("============ 开始执行 {}.{} ============", joinPoint.getTarget().getClass(), joinPoint.getSignature().getName());

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long takeTime = end - start;

        if (takeTime > 3000) {
            log.error("当前执行耗时：{}", takeTime);
        } else if (takeTime > 2000) {
            log.warn("当前执行耗时：{}", takeTime);
        } else {
            log.info("当前执行耗时：{}", takeTime);
        }
        return result;
    }
}
