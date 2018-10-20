package com.s1coder.cat.bean;

import com.s1coder.cat.error.CatException;
import com.s1coder.cat.error.UtilsParamException;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 类名: PojoCopyUtil <br/>
 * 用途: 类拷贝工具,自动将同名同类型属性拷贝 <br/>
 * 注意: 只对类本身的属性起作用,父类的属性不会进行操作
 *
 * @author wangbo <br/>
 * Oct 20, 2017 4:17:30 PM
 * @version 1.1 支持enum属性和string属性的相互转换
 */
public class PojoCopyUtil {

    private PojoCopyUtil() {

    }

    /**
     * 使用反射拷贝相同属性
     *
     * @param org  原始对象
     * @param dest 拷贝对象
     * @throws CatException
     */
    public static void copy(Object org, Object dest) throws CatException {
        if (org == null) {
            throw new UtilsParamException("org不能为空");
        }
        if (dest == null) {
            throw new UtilsParamException("dest不能为空");
        }
        Field[] fields = dest.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (fieldName.contains("jacocoData")) {
                    continue;
                }

                Class<?> type = field.getType();

                Field srcField = org.getClass().getDeclaredField(fieldName);
                Class<?> srctype = srcField.getType();
                srcField.setAccessible(true);
                Object orgObj = srcField.get(org);
                if (type.getName().equals(srctype.getName())) {
                    field.set(dest, orgObj);
                } else if (orgObj != null && srctype.isEnum() && type.isAssignableFrom(String.class)) {
                    field.set(dest, ((Enum<?>) orgObj).name());
                } else if (orgObj != null && type.isEnum() && srctype.isAssignableFrom(String.class)) {
                    field.set(dest, Enum.valueOf((Class<? extends Enum>) type, (String) orgObj));
                } else if (orgObj != null && srctype.isAssignableFrom(byte[].class) && type.isAssignableFrom(String.class)) {
                    field.set(dest, new String((byte[]) orgObj));
                } else if (orgObj != null && type.isAssignableFrom(byte[].class) && srctype.isAssignableFrom(String.class)) {
                    field.set(dest, ((String) orgObj).getBytes());
                }
            } catch (Exception e) {
                //异常不做处理
            }
        }
    }

    /**
     * 检查所有属性是否为空
     *
     * @param obj
     * @throws CatException 任何属性为空抛出异常
     */
    public static void checkNull(Object obj) throws CatException {
        checkNull(obj, null);
    }

    /**
     * @param obj         检查对象
     * @param uncheckList 不进行检查的属性
     * @throws CatException
     */
    public static void checkNull(Object obj, List<String> uncheckList) throws CatException {
        if (obj == null) {
            throw new UtilsParamException("判断对象为空");
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        Set<String> uncheck = new HashSet<>();
        if (uncheckList != null) {
            for (String str : uncheckList) {
                uncheck.add(str);
            }
        }


        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.contains("jacocoData") || uncheck.contains(fieldName)) {
                //为兼容集成测试环境jacoco运行环境,修复此问题
                continue;
            }
            try {
                field.setAccessible(true);
                if (null == field.get(obj)) {
                    throw new UtilsParamException("对象:" + fieldName + " 属性为空");
                }

            } catch (CatException e) {
                throw e;
            } catch (Exception e) {
                //其他异常不做处理
            }
        }

    }
}
