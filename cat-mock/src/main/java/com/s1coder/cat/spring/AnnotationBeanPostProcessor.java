package com.s1coder.cat.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class AnnotationBeanPostProcessor extends PropertyPlaceholderConfigurer
        implements BeanPostProcessor, InitializingBean {
    private java.util.Properties pros;

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean.getClass().getAnnotation(Property.class) != null) {
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Property p = method.getAnnotation(Property.class);
                if (p != null) {
                    Object para = pros.getProperty(p.name());
                    if ((method.getParameterTypes()[0]).getName().equals(
                            "java.lang.Integer")) {
                        para = new Integer(para.toString());
                    } else if ((method.getParameterTypes()[0]).getName().equals(
                            "java.lang.Long")) {
                        para = new Long(para.toString());
                    }
                    ReflectionUtils.invokeMethod(method, bean,
                            new Object[]{para});
                }
            }
        }
        return bean;
    }

    public void afterPropertiesSet() throws Exception {
        pros = mergeProperties();
    }
}
