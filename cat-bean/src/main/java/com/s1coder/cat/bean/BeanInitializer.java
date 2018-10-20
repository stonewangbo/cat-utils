package com.s1coder.cat.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * 类名: BeanInitializer <br/>
 * 用途: 测试用类初始化工具,自动填充值 <br/>
 *
 * @author wangbo, wangran <br/>
 * Dec 26, 2017 4:08:38 PM
 */
public class BeanInitializer {
    private static Calendar cal = Calendar.getInstance();

    private static final Logger LOG = LoggerFactory.getLogger(BeanInitializer.class);
    public static final String DEFAULT_STRING = "1";
    public static final int DEFAULT_INT = 1;
    public static final double DEFAULT_DOUBLE = 1.0d;
    public static final float DEFAULT_FLOAT = 1f;
    public static final long DEFAULT_LONG = 1l;
    public static final short DEFAULT_SHORT = (short) 1;
    public static final char DEFAULT_CHAR = 'a';
    public static final Date DEFAULT_DATE = cal.getTime();
    public static final BigDecimal DEFAULT_BIGDECIMAL = BigDecimal.valueOf(0.1);

    public static final String UPDATED_STRING = "1";
    public static final int UPDATED_INT = 1;
    public static final double UPDATED_DOUBLE = 1.0d;
    public static final float UPDATED_FLOAT = 1f;
    public static final long UPDATED_LONG = 1l;
    public static final short UPDATED_SHORT = (short) 1;
    public static final char UPDATED_CHAR = 'z';
    public static final Date UPDATED_DATE = cal.getTime();
    public static final BigDecimal UPDATED_BIGDECIMAL = BigDecimal.valueOf(0.1);

    private BeanInitializer() {
    }

    static {
        cal.add(Calendar.DAY_OF_YEAR, 1);
    }

    /**
     * 给对象填充初始化值
     *
     * @param object
     */
    public static void init(Object object) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (fieldName.equals("serialVersionUID")) {
                        continue;
                    }
                    Class<?> type = field.getType();
                    String typeName = type.getSimpleName();
                    Object value = genDefaultValue(type, typeName);
                    field.set(object, value);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 更新对象初始化值
     *
     * @param object
     */
    public static void update(Object object) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();

                // do not update columns contain 'id'
                if (fieldName.toLowerCase().contains("id")) {
                    continue;
                }

                Class<?> type = field.getType();
                String typeName = type.getSimpleName();
                Object value = genUpdatedValue(type, typeName);
                field.set(object, value);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static Object genUpdatedValue(Class<?> type, String typeName) throws InstantiationException, IllegalAccessException {
        switch (typeName) {
            case "String":
                return UPDATED_STRING;
            case "Integer":
                return UPDATED_INT;
            case "Double":
                return UPDATED_DOUBLE;
            case "Float":
                return UPDATED_FLOAT;
            case "Long":
                return UPDATED_LONG;
            case "Short":
                return UPDATED_SHORT;
            case "Character":
                return UPDATED_CHAR;
            case "Date":
                return UPDATED_DATE;
            case "BigDecimal":
                return UPDATED_BIGDECIMAL;
            case "Boolean":
                return true;
            default:
                return type.newInstance();
        }
    }

    private static Object genDefaultValue(Class<?> type, String typeName) throws InstantiationException, IllegalAccessException {

        switch (typeName) {
            case "String":
                return DEFAULT_STRING;
            case "Integer":
                return DEFAULT_INT;
            case "Double":
                return DEFAULT_DOUBLE;
            case "Float":
                return DEFAULT_FLOAT;
            case "Long":
                return DEFAULT_LONG;
            case "Short":
                return DEFAULT_SHORT;
            case "Character":
                return DEFAULT_CHAR;
            case "Date":
                return DEFAULT_DATE;
            case "BigDecimal":
                return DEFAULT_BIGDECIMAL;
            case "Boolean":
                return true;
            default:
                return type.newInstance();
        }
    }
}
