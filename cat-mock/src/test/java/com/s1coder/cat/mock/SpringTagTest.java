package com.s1coder.cat.mock;

import com.s1coder.cat.mock.sample.TagTestService;
import com.s1coder.cat.mock.sample.TestInterfaceFacade;
import com.s1coder.cat.mock.sample.TestServiceFacade;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类名: SpringTagTest <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Jan 4, 2018 3:50:07 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-init-test.xml"
})
public class SpringTagTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private TagTestService ragTestService;

    @Mock
    private TestInterfaceFacade testInterfaceFacade;

    @Mock
    private TestServiceFacade testServiceFacade;


    private CatMock catMock = new CatMock();

    @Before
    public void init() throws Exception {
        //初始化Mockito
        MockitoAnnotations.initMocks(this);
        //初始化自动mock工具,注意需要在mock工具初始化完毕后再调用本工具
        catMock.mockAll(this);
    }

    @After
    public void close() throws Exception {
        catMock.reset(this);
    }


    @Test
    public void test() {
        Mockito.when(testServiceFacade.call(Mockito.anyString())).thenReturn("test");
        Mockito.when(testInterfaceFacade.call(Mockito.anyString())).thenReturn("test");

        Assert.assertEquals(ragTestService.callInterface(""), "test");
        Assert.assertEquals(ragTestService.callService(""), "test");
        ;

    }
}
