package org.springframework.boot.autoconfigure.klock.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.klock.annotation.Klock;
import org.springframework.boot.autoconfigure.klock.lock.Lock;
import org.springframework.boot.autoconfigure.klock.lock.LockFactory;
import org.springframework.stereotype.Component;

/**
 * Created by kl on 2017/12/29.
 * Content :给添加@KLock切面加锁处理
 */
@Aspect
@Component
public class KlockAspectHandler {

    @Autowired
    LockFactory lockFactory;

    @Around(value = "@annotation(klock)")
    public Object around(ProceedingJoinPoint joinPoint, Klock klock) throws Throwable {
        //获取锁对象（可重入锁，公平锁，读锁，写锁）
        Lock lock = lockFactory.getLock(joinPoint,klock);
        boolean currentThreadLock = false;
        try {
            //尝试拿锁
            currentThreadLock = lock.acquire();
            //执行目标方法
            return joinPoint.proceed();   //扩展 finally是在return语句执行之后 返回之前  这中间执行
        } finally {
            if (currentThreadLock) {
                lock.release();
            }
        }
    }
}
