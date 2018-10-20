package com.s1coder.cat.mock;

import com.s1coder.cat.error.CatException;
import com.s1coder.cat.error.UtilsParamException;
import com.s1coder.cat.thread.NotReEnterLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


/**
 * 类名: CatMock <br>
 * 用途: 自动mock工具,可以将测试用例中mock的同名实例,自动替换程序内部嵌套的属性 <br>
 * 支持: 目前支持替换autowired,Resource注解,<br>
 * 支持mockito easymock框架中Mock注解
 *
 * @author wangbo <br/>
 * Dec 25, 2017 11:10:12 AM
 */
public class CatMock {
    private static final Logger logger = LoggerFactory.getLogger(CatMock.class);


    private Map<Field, Object> mockObject = new HashMap<>();

    private Set<String> userMock = new HashSet<>();


    private List<MockItem> res = new ArrayList<>();

    private Set<Object> mocked = new HashSet<>();

    static final NotReEnterLock lock = new NotReEnterLock();

    /**
     * 手动添加需要mock的数据<br>
     * 注意:请在mockAll之前调用此方法,否则不能生效
     *
     * @param mockType  需要替换属性的类
     * @param fieldName 替换的属性名称
     * @param mock      替换用mock对象
     * @throws UtilsParamException
     */
    public void addMock(Class<?> mockType, String fieldName, Object mock) {
        boolean mocked = false;
        Field[] fields = mockType.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
//                if(!mock.getClass().isAssignableFrom(field.getType())){
//                    throw new UtilsParamException("对象属性:"+field.getType().getName()+" 与提供的mock对象不符:"+mock.getClass().getName());
//                }
                mockObject.put(field, mock);
                userMock.add(getMockKey(mockType, field));
                mocked = true;
                break;
            }
        }
        if (!mocked) {
            throw new UtilsParamException("没有在 mockType:" + mockType + " 中找到需mock的属性:" + fieldName);
        }
    }

    /**
     * 请在mock框架完成初始化之后调用此方法<br>
     * 调用此方法单元测试结束后,需调用reset方法
     *
     * @param testContext
     * @throws InterruptedException
     * @throws Exception
     */
    public void mockAll(final Object testContext) throws InterruptedException {
        Class<?> targetClass = null;
        try {
            targetClass = AopTargetUtils.getTarget(testContext).getClass();
        } catch (Exception e1) {
            logger.warn("get testContext fail:", e1);
        }
        lock.lock(targetClass == null ? testContext + "" : targetClass.getName());
        try {
            injectMock(testContext, true);
        } catch (Exception e) {
            throw new CatException("mock失败:", e);
        }
    }

    /**
     * 还原mock过的数据,调用mockAll之后单元测试结束,需调用此方法
     *
     * @param testContext
     * @throws Exception
     */
    public void reset(final Object testContext) {
        for (MockItem item : res) {
            ReflectionTestUtils.setField(item.taget, item.field, item.orgVal);
        }
        lock.unlock();
    }

    private String getMockKey(Class<?> mockType, Field fieldName) {
        return mockType.getName() + "." + fieldName;
    }


    @SuppressWarnings("rawtypes")
    private void injectMock(Object bean, boolean top) throws Exception {
        if (bean == null) {
            return;
        }
        if (mocked.contains(bean)) {
            return;
        } else {
            mocked.add(bean);
        }

        Field[] fields;
        /*找到所有的测试用例的字段*/

        Class targetClass = AopTargetUtils.getTarget(bean).getClass();
        if (targetClass == null) {
            // 可能是远程实现  
            return;
        }
        fields = targetClass.getDeclaredFields();


        Map<String, Field> injectFields = new HashMap<>();

        /*判断字段上的注解*/
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation antt : annotations) {
                /*如果是Mock字段的,就直接注入Mock的对象*/
                if ((antt.toString().contains("org.mockito.Mock") ||
                        antt.toString().contains("org.easymock.Mock") ||
                        antt.toString().contains("mockit.Mocked")) && top) {
                    logger.info("annt:{} {}", antt, antt.getClass().getName());
                    field.setAccessible(true);
                    // 获取mock实例  
                    Object mockObj = field.get(bean);
                    mockObject.put(field, mockObj);
                    field.set(bean, mockObj);
                    // } else if (antt.toString().contains("Autowired") || antt instanceof Resource
                    //         || antt.toString().contains("org.easymock.TestSubject")
                    //         || antt.toString().contains("org.mockito.InjectMocks")
                    //        || antt.toString().contains("mockit.Tested")) {
                } else if (!injectFields.containsKey(bean.hashCode() + field.getName())) {
                    /*所有非mock属性都加入替换列表*/
                    injectFields.put(bean.hashCode() + field.getName(), field);
                }
            }
            if (!top) {
                try {
                    //field.setAccessible(true);            
                    /*找到每一个字段的值*/
                    //Object object = field.get(bean);   
                    //if(object instanceof Map ||  object instanceof  Iterable || userMock.contains(getMockKey(bean.getClass(), field))){
                    if (!injectFields.containsKey(bean.hashCode() + field.getName())) {
                        injectFields.put(field.getType().getName() + field.getName(), field);
                    }
                } catch (Exception e) {
                    logger.warn("bean:{} field:{} fail:{}", bean, field, e.getMessage());
                }
            }
        }

        /*访问每一个可能需替换的属性*/
        for (Field field : injectFields.values()) {
            try {
                field.setAccessible(true);
                /*找到每一个字段的值*/
                Object object = field.get(bean);
                if (object instanceof Map) {
                    for (Object obj : ((Map) object).values()) {
                        injectMock(obj, false);
                    }
                } else if (object instanceof Iterable) {
                    for (Object obj : ((Iterable) object)) {
                        injectMock(obj, false);
                    }
                } else if (!replaceInstance(field, bean, mockObject)) {
                    //如果没有被mock过.那么这个字段需要再一次的做递归
                    injectMock(object, false);
                }
            } catch (Exception e) {
                logger.warn("bean:{} field:{} fail:{}", bean, field, e.getMessage());
            }
        }
    }


    boolean replaceInstance(Field targetField, Object bean, Map<Field, Object> mockObject) throws Exception {
        boolean beMocked = false;
        for (Map.Entry<Field, Object> classObjectEntry : mockObject.entrySet()) {
            Field field = classObjectEntry.getKey();
            if (field.getType().equals(targetField.getType())
                    && field.getName().equals(targetField.getName())) {
                Object taget = AopTargetUtils.getTarget(bean);
                MockItem item = new MockItem();
                item.field = targetField.getName();
                item.taget = taget;
                item.orgVal = ReflectionTestUtils.getField(taget, targetField.getName());
                res.add(item);
                //如果这个字段是被mock了的对象.那么就使用这个mock的对象来替换
                ReflectionTestUtils.setField(taget, targetField.getName(), classObjectEntry.getValue());
                beMocked = true;
                break;
            }
        }
        return beMocked;
    }

    public class MockItem {
        Object taget;
        String field;
        Object orgVal;
    }


}
