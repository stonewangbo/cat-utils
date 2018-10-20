package com.s1coder.cat.bean;

import com.s1coder.cat.error.CatException;

/**
 * 类名: CatBeanUtil <br/>
 * 用途: bean操作复制校验工具 <br/>
 *
 * @author wangbo <br/>
 * Jan 16, 2018 3:23:35 PM
 */
public class CatBeanUtil {

    private CatBeanUtil() {
    }

    /**
     * 初始化bean字段
     *
     * @param object
     * @throws CatException
     */
    public static void init(Object object) throws CatException {
        BeanInitializer.init(object);
    }

    public static void update(Object object) throws CatException {
        BeanInitializer.update(object);
    }

    public static void copy(Object org, Object dest) throws CatException {
        PojoCopyUtil.copy(org, dest);
    }

    public static void checkNull(Object obj) throws CatException {
        PojoCopyUtil.checkNull(obj);
    }
}
