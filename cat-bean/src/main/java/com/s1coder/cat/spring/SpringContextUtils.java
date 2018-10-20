package com.s1coder.cat.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * 类名: SpringContextUtils <br/>
 * 用途: 获取SpringContext <br/>
 *
 * @author wangbo <br/>
 * Sep 14, 2016 6:14:15 PM
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }


}
