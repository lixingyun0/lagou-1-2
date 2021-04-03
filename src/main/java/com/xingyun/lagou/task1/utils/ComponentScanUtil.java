package com.xingyun.lagou.task1.utils;

import com.xingyun.lagou.task1.annotation.MyService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @author xingyun
 * @date 2021/4/2
 */
public class ComponentScanUtil {


    public  static void main(String[] args) {
        String scanPackage = "com.xingyun.lagou.taks1";
        GenericApplicationContext context = new GenericApplicationContext();
        MyClassPathDefinitonScanner myClassPathDefinitonScanner = new MyClassPathDefinitonScanner(context, MyService.class);
        // 注册过滤器
        myClassPathDefinitonScanner.registerTypeFilter();
        int beanCount = myClassPathDefinitonScanner.scan(scanPackage);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        System.out.println(beanCount);
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

    static class MyClassPathDefinitonScanner extends ClassPathBeanDefinitionScanner{
        private Class type;
        public MyClassPathDefinitonScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> type){
            super(registry,false);
            this.type = type;
        }
        /**
         * 注册 过滤器
         */
        public void registerTypeFilter(){
            addIncludeFilter(new AnnotationTypeFilter(type));
        }
    }
}
