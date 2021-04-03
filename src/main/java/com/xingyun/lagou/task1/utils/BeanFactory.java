package com.xingyun.lagou.task1.utils;

import com.xingyun.lagou.task1.annotation.MyAutowired;
import com.xingyun.lagou.task1.annotation.MyService;
import com.xingyun.lagou.task1.annotation.MyTransactional;
import com.xingyun.lagou.task1.dto.Account;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xingyun
 * @date 2021/3/30
 */
public class BeanFactory {

    private static Map<String,Object> container = new HashMap<>();

    static {
        loadFromXML();
        loadFromAnnotation();
    }
    private static void loadFromAnnotation(){
        String basePackage = "com.xingyun.lagou.task1";

        //扫描包下面带有自定义注解的所有类
        List<String> classNameList = classNameList(basePackage);

        //需要注入依赖的beanId
        Set<String> beanNeedPropertyIdList = new HashSet<>();

        //需要生成代理类的beanId
        Set<String> beanNeedProxyIdList = new HashSet<>();

        //实例化bean并放入容器

        for (String className : classNameList) {

            try {
                Class<?> aClass = Class.forName(className);
                MyService annotation = aClass.getAnnotation(MyService.class);
                //获取MyService注解的value值，如果不为空，则bean的ID为value
                String value = annotation.value();
                Object o = aClass.newInstance();

                //如果value为空，则beanId为类名的首字母小写
                if (StringUtils.isBlank(value)){
                    value = getBeanName(className);
                }

                container.put(value,o);
                //获取需要注入属性或者生成代理的beanId，放入set集合
                needPropertyOrProxy(value,beanNeedPropertyIdList,beanNeedProxyIdList);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        //注入bean的依赖
        doDI(beanNeedPropertyIdList);
        //生成代理类
        generateProxy(beanNeedProxyIdList);
    }
    private static void doDI(Set<String> beanNeedPropertyIdList){
        for (String beanId : beanNeedPropertyIdList) {
            Object o = container.get(beanId);
            Class<?> aClass = o.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {

                MyAutowired annotation = declaredField.getAnnotation(MyAutowired.class);
                if (annotation != null){
                    Class<?> type = declaredField.getType();
                    declaredField.setAccessible(true);

                    try {
                        declaredField.set(o,findProperty(type));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    private static void generateProxy(Set<String> beanNeedProxyIdList){
        for (String beanId : beanNeedProxyIdList) {

            Object target = container.get(beanId);
            Class<?>[] interfaces = target.getClass().getInterfaces();

            //实现接口用jdk动态代理 ，否则用cglib
            if (interfaces.length>0){

                //TODO 使用jdk动态代理，无法获取代理的目标类上的注解。
                //TODO 暂时统一使用cglib动态代理，后续找到解决办法再修正
                //Object jdkProxy = ProxyFactory.getJdkProxy(target);
                container.put(beanId,ProxyFactory.getCglibProxy(target));

            }else {
                container.put(beanId,ProxyFactory.getCglibProxy(target));

            }

        }
    }


    private static Object findProperty(Class<?> aClass){
        for (Map.Entry<String, Object> stringObjectEntry : container.entrySet()) {
            Class<?> aClass1 = stringObjectEntry.getValue().getClass();
            if (aClass.isAssignableFrom(aClass1)){
                return stringObjectEntry.getValue();
            }
        }
        return null;
    }
    private static List<String> classNameList(String basePackages){
        List<String> classNameList = new ArrayList<>();

        List<MetadataReader> metaDataReaderList = getMetaDataReaderList(basePackages);

        for (MetadataReader metadataReader : metaDataReaderList) {

            String className = metadataReader.getClassMetadata().getClassName();
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();


            boolean b = annotationMetadata.hasAnnotation("com.xingyun.lagou.task1.annotation.MyService");
            if (b){
                System.out.println(className);
                classNameList.add(className);
            }
        }
        return classNameList;

    }

    private static List<MetadataReader> getMetaDataReaderList(String basePackage){
        String resourcePattern = "**/*.class";
        String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

        String resourcePath = basePackage.replace('.','/');

        String packageSearchPath = CLASSPATH_ALL_URL_PREFIX +
                resourcePath + '/' + resourcePattern;

        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = new Resource[0];
        try {
            resources = pathMatchingResourcePatternResolver.getResources(packageSearchPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<MetadataReader> metadataReaderList = new ArrayList<>();
        CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                try {

                    MetadataReader metadataReader = cachingMetadataReaderFactory.getMetadataReader(resource);
                    metadataReaderList.add(metadataReader);
                    String className = metadataReader.getClassMetadata().getClassName();
                    AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

                    boolean b = annotationMetadata.hasAnnotation("org.springframework.context.annotation.Configuration");
                    if (b){
                        System.out.println(className);
                    }
                }
                catch (Throwable ex) {
                    throw new BeanDefinitionStoreException(
                            "Failed to read candidate component class: " + resource, ex);
                }
            }
        }
        return metadataReaderList;

    }

    public static  <T> T getBeanById(String id){
        return (T) container.get(id);
    }


    private static void loadFromXML(){
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");

        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Node> nodes = rootElement.selectNodes("//bean");
            for (Node node : nodes) {

                String id = ((Element) node).attributeValue("id");
                String clazz = ((Element) node).attributeValue("class");

                Class<?> aClass = Class.forName(clazz);
                Object o = aClass.newInstance();
                container.put(id,o);

            }

            //注入依赖
            for (Node node : nodes) {
                List<Node> propertyList = node.selectNodes("//property");

                for (Node property : propertyList) {
                    Element parent = property.getParent();
                    String id = parent.attributeValue("id");
                    Object o = container.get(id);

                    String name = ((Element) property).attributeValue("name");
                    String ref = ((Element) property).attributeValue("ref");

                    for (Method declaredMethod : o.getClass().getDeclaredMethods()) {
                        if (declaredMethod.getName().equalsIgnoreCase("set"+name)){
                            declaredMethod.setAccessible(true);
                            declaredMethod.invoke(o,container.get(ref));
                        }
                    }

                }
            }
        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static String getBeanName(String className){
        String substring = className.substring(className.lastIndexOf(".")+1);
        return substring.substring(0,1).toLowerCase() + substring.substring(1);
    }

    private static void needPropertyOrProxy(String beanId,Set<String> beanNeedPropertyIdList,Set<String> beanNeedProxyIdList){
        Class<?> aClass = container.get(beanId).getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        //属性上有MyAutowired ，则需要注入属性
        for (Field declaredField : declaredFields) {

            MyAutowired annotation = declaredField.getAnnotation(MyAutowired.class);
            if (annotation != null){
                beanNeedPropertyIdList.add(beanId);
            }
        }
        //类或者方法上有MyTransactional 则需要生成代理
        MyTransactional annotation = aClass.getAnnotation(MyTransactional.class);
        if (annotation != null){
            beanNeedProxyIdList.add(beanId);
        }
        for (Method declaredMethod : aClass.getDeclaredMethods()) {
            MyTransactional annotation1 = declaredMethod.getAnnotation(MyTransactional.class);
            if (annotation1 != null){
                beanNeedProxyIdList.add(beanId);
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException {
        Class<?> impl = Account.class;

        Class<?>[] interfaces = impl.getInterfaces();

        System.out.println(interfaces.length);

        for (Class<?> anInterface : interfaces) {
            System.out.println(anInterface);
        }

    }

}
