package com.xingyun.lagou.task1.utils;

import com.xingyun.lagou.task1.annotation.MyTransactional;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author xingyun
 * @date 2021/3/30
 */
public class ProxyFactory {

    final static String AOP_JAVA__TARGET_FIELD = "h";
    final static String AOP_CGLIB_TARGET_FIELD = "CGLIB$CALLBACK_0";

    public static <T> T getJdkProxy(Object target){

        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                Connection connection = ConnectionUtils.getConnection();
                Object invoke = null;
                try {
                    connection.setAutoCommit(false);
                    invoke = method.invoke(target, args);
                    connection.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    connection.rollback();
                }
                return invoke;
            }
        });

    }

    public static <T> T getCglibProxy(Object target){
        return (T) Enhancer.create(target.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

                //类或者方法上是否有MyTransactional注解
                //有的话使用事务增强， 没有直接执行
                if (!isTransactional(o,method)){
                    return method.invoke(target,objects);
                }

                Connection connection = ConnectionUtils.getConnection();
                Object invoke = null;
                try {
                    connection.setAutoCommit(false);
                    invoke = method.invoke(target, objects);
                    connection.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    connection.rollback();
                }
                return invoke;
            }
        });
    }

    private static Boolean isTransactional(Object proxy, Method method){
        boolean transactional = false;
        if (null != method.getDeclaringClass().getDeclaredAnnotation(MyTransactional.class)
                ||null != method.getDeclaredAnnotation(MyTransactional.class)){
            return true;
        }
        return transactional;
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy){
        try{
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            /*AopProxy aopProxy = (AopProxy) h.get(proxy);
            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();*/
            return proxy;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField(AOP_CGLIB_TARGET_FIELD);
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass()
                .getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor))
                .getTargetSource().getTarget();

        return target;
    }


}
