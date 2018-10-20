package com.s1coder.cat.mock;

import com.s1coder.cat.mock.sample.SampleOtherSysFacade;
import com.s1coder.cat.mock.sample.SampleSecondService;
import com.s1coder.cat.mock.sample.SampleTopService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类名: GlobalMockUtilTest <br/>
 * 用途: 测试工具,同时作为使用示例 <br/>
 *
 * @author wangbo <br/>
 * Dec 26, 2017 5:03:07 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-init-test.xml"
})
public class CatMockTest {
    /**
     * 被测试的业务,让spring框架自动注入即可
     */
    @Autowired
    private SampleTopService sampleTopService;

    /**
     * 需mock的外部服务,支持mock任意层级调用的服务,支持easymock和mockito的Mock注解 <br>
     * 注意: GlobalMockUtil工具的mock规则会将有mock注解的mock服务,替换当前spring加载路径中,<br>
     * 所有同类型,同名的属性,请注意属性名需要和被mock服务相同
     */
    @Mock
    private SampleOtherSysFacade sampleOtherSysFacade;

    //测试同一个服务不同的引用名称
    @Mock
    private SampleOtherSysFacade sampleOtherSysFacade3;


    private CatMock catMock = new CatMock();

    @Before
    public void init() throws Exception {
        //初始化Mockito
        MockitoAnnotations.initMocks(this);
        //手动替换需mock的属性
        catMock.addMock(SampleSecondService.class, "sampleOtherSysFacade", sampleOtherSysFacade);
        //初始化自动mock工具,注意需要在mock工具初始化完毕后再调用本工具
        catMock.mockAll(this);
    }

    @After
    public void close() throws Exception {
        catMock.reset(this);
    }

    @Test
    public void test() {
        String str = "test res";
        String str3 = "test res3";
        //mock 外部系统方法
        Mockito.when(sampleOtherSysFacade.callOtherSys(Mockito.anyString())).thenReturn(str);
        Mockito.when(sampleOtherSysFacade3.callOtherSys(Mockito.anyString())).thenReturn(str3);

        //注意,这个sampleTopService并不是直接调用sampleOtherSysFacade
        //而是通过其他的内部业务逻辑间接调用到了sampleOtherSysFacade,使用本工具可以mock这些间接引用的服务
        String res = sampleTopService.callService2("test");

        Assert.assertEquals(str, res);

        res = sampleTopService.callMapInterService("test");
        Assert.assertEquals(str, res);

        res = sampleTopService.callListInterService("test");
        Assert.assertEquals(str, res);

        res = sampleTopService.callService3("test");
        Assert.assertEquals(str3, res);
    }
}
