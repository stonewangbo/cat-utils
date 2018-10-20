package com.s1coder.cat.redis;

import com.s1coder.cat.error.CatException;
import com.s1coder.cat.mock.CatMock;
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
 * 类名: RedisLockUtilsTest <br/>
 * 用途: 功能测试类,连接测试环境redis集群 <br/>
 *
 * @author wangbo <br/>
 * Jan 31, 2018 5:41:12 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-mock-test.xml"
})
public class RedisLockUtilsMockTest {
    private CatMock catMock = new CatMock();

    @Autowired
    private RedisLockUtils redisLockUtils;

    @Mock
    private RedisClient redisClient;

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
    public void test() throws Exception {
        String key = "test";
        //
        Mockito.when(redisClient.setNx(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(true);

        try (RedisLocker locker = redisLockUtils.lock(key, 5)) {
            Assert.assertTrue(locker.isLocked());

        } catch (CatException e) {
            Assert.assertFalse("不应抛出异常", true);
        }

    }


}
