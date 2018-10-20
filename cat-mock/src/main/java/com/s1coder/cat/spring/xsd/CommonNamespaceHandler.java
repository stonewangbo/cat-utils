package com.s1coder.cat.spring.xsd;

import javassist.*;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.VisibilityPredicate;
import net.sf.cglib.proxy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 类名: CommonNamespaceHandler <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Jan 4, 2018 3:27:22 PM
 */
public class CommonNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        this.registerBeanDefinitionParser("service",
                new MockServerDefinitionParser());
    }
}

class MockServerDefinitionParser implements BeanDefinitionParser {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String default_prefix = "catMock-";
    private static final AtomicLong COUNT = new AtomicLong(0);

    public MockServerDefinitionParser() {

    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parseHelper(element, parserContext);
    }

    private BeanDefinition parseHelper(Element element, ParserContext parserContext) {
        RootBeanDefinition bd = new RootBeanDefinition();

        bd.setLazyInit(false);
        String id = element.getAttribute("id");
        if (id == null || id.isEmpty()) {
            id = default_prefix + COUNT.getAndDecrement();
        }

        final String className = element.getAttribute("type");
        Class<?> target = null;
        try {
            target = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("can not find classs:" + className + " plase check your mock type config", e);
        }


        Enhancer enhancer = new Enhancer() {
            /**
             * Filter all private constructors but do not check that there are
             * some left
             */
            @Override
            protected void filterConstructors(Class sc, List constructors) {
                CollectionUtils.filter(constructors, new VisibilityPredicate(
                        sc, true));
            }
        };
        if (target.isInterface()) {
            enhancer.setInterfaces(new Class[]{target});
            enhancer.setSuperclass(Object.class);
        } else {
            enhancer.setSuperclass(target);
        }


        class MyMethodInterceptor implements MethodInterceptor {
            public Object intercept(Object obj, Method method, Object[] arg, MethodProxy proxy) throws Throwable {
                logger.info("mock类被调用,不执行任何方法 obj:{} method:{}", obj, method);
                try {
                    return method.getReturnType().newInstance();
                } catch (Exception e) {
                    return NoOp.INSTANCE;
                }
            }
        }


        enhancer.setCallbackTypes(new Class[]{MethodInterceptor.class, NoOp.class});
        enhancer.setCallbackFilter(IGNORE_BRIDGE_METHODS);

        //enhancer.setCallback(new MyMethodInterceptor());      
        enhancer.setInterceptDuringConstruction(true);
//        enhancer.setStrategy(new DefaultGeneratorStrategy() {
//            @Override
//            protected ClassGenerator transform(ClassGenerator cg) throws Exception {
//                return new TransformingClassGenerator(cg, new DefaultConstructorEmitter(className));
//            }
//        });
        Class<?> cgclass = enhancer.createClass();

        try {
            if (!target.isInterface()) {
                try {
                    Class<?> jsClass = createObjectMock(target);
                    bd.setBeanClass(jsClass);
                } catch (Exception e) {
                    logger.error("javassist fail :", e);
                    bd.setBeanClass(cgclass);
                }
            } else {
                bd.setBeanClass(cgclass);
            }


            bd.setLazyInit(true);
            bd.setEnforceInitMethod(false);

            //bd.setInitMethodName("init");


            MutablePropertyValues propertyValues = bd.getPropertyValues();
            //propertyValues.addPropertyValue("serverName", serverName);

            parserContext.getRegistry().registerBeanDefinition(id, bd);
        } catch (Exception e) {
            logger.error("fail:", e);
        }

        return bd;
    }

    private void fixConstructors(CtClass ctClass) throws CannotCompileException, NotFoundException {
        boolean hasDefault = false;
        for (CtConstructor ctConstructor : ctClass.getConstructors()) {
            try {
                fixConstructor(ctClass, hasDefault, ctConstructor);
                if (ctConstructor.getParameterTypes().length == 0) {
                    hasDefault = true;
                }
            } catch (Exception e) {
                throw new RuntimeException("problem instrumenting " + ctConstructor, e);
            }
        }
        if (!hasDefault) {
            String methodBody = "";
            CtConstructor ct = CtNewConstructor.make(new CtClass[0], new CtClass[0], "{\n" + methodBody + "}\n", ctClass);
            ct.setModifiers(Modifier.PUBLIC);
            ctClass.addConstructor(ct);
        }
    }

    private boolean fixConstructor(CtClass ctClass, boolean needsDefault, CtConstructor ctConstructor) throws NotFoundException, CannotCompileException {
        String methodBody = "";
        ctConstructor.setBody("{\n" + methodBody + "}\n");
        ctConstructor.setModifiers(Modifier.PUBLIC);
        return needsDefault;
    }


    private static final CallbackFilter IGNORE_BRIDGE_METHODS = new CallbackFilter() {
        public int accept(Method method) {
            return method.isBridge() ? 1 : 0;
        }
    };

    private Class<?> createObjectMock(Class<?> target) throws CannotCompileException, RuntimeException, NotFoundException {
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


        return ctClass.toClass();
    }

}
