package com.s1coder.cat.mock;

import com.s1coder.cat.mock.sample.TestServiceFacade;
import javassist.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * 类名: CreateClassTest <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Jan 8, 2018 3:33:46 PM
 */
public class CreateClassTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test() throws RuntimeException, NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
        Class<?> target = TestServiceFacade.class;
        ClassPool classPool = new ClassPool(true);
        CtClass ctClass = classPool.makeClass(
                target.getName() + "_ct" + System.currentTimeMillis(), classPool.get(target.getName()));
        Constructor<?> targetCon = null;
        //判断和尝试获取父类默认构造函数
        for (Constructor<?> item : target.getConstructors()) {
            if (item.getParameterTypes().length == 0) {
                targetCon = item;
                break;
            } else if (targetCon == null || targetCon.getParameterTypes().length >= item.getParameterTypes().length) {
                targetCon = item;
            }
        }


        if (targetCon == null || targetCon.getParameterTypes().length == 0) {
            ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
        } else {
            StringBuilder str = new StringBuilder().append("public ").append(ctClass.getSimpleName()).append("() { super(");
            int i = 0;
            for (Class<?> item : targetCon.getParameterTypes()) {
                //new TestServiceFacade((item)org.objenesis.ObjenesisHelper.newInstance(item)) ;
                if (i > 0) {
                    str.append(",");
                }
                str.append(" (").append(item.getName()).append(")").append("org.objenesis.ObjenesisHelper.newInstance(").append(item.getName()).append(".class)");
                ++i;
            }
            str.append(");}");


            try {
                CtConstructor defaultConstructor = CtNewConstructor
                        .make(str.toString(), ctClass);
                defaultConstructor.setModifiers(Modifier.PUBLIC);
                ctClass.addConstructor(defaultConstructor);
            } catch (Throwable e) {
                logger.error("new class fail str:{}", str, e);
            }
        }


        Class<?> ct = ctClass.toClass();
        for (Constructor<?> constr : ct.getConstructors()) {
            constr.getParameterTypes();
            logger.info("Constructor:{}", constr.getParameterTypes());
        }


        logger.info("newInstance:{}", ct.newInstance());
    }
}
