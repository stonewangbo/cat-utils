package com.s1coder.cat.redis;

import com.s1coder.cat.error.CatException;
import com.s1coder.cat.mock.CatMock;
import com.s1coder.cat.thread.AsynchronousCallUtil;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类名: RedisLockUtilsTest <br/>
 * 用途: 功能测试类,连接测试环境redis集群 <br/>
 *
 * @author wangbo <br/>
 * Jan 31, 2018 5:41:12 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-init-test.xml"
})
@Ignore
public class RedisLockUtilsTest {
    private CatMock catMock = new CatMock();

    @Resource(name = "catRedisClient")
    private RedisClient redisClient;


    @Autowired
    private RedisLockUtils redisLockUtils;

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
        //并发调度工具:
        AsynchronousCallUtil asynccall = new AsynchronousCallUtil();
        //
        try (RedisLocker locker = redisLockUtils.lock(key, 5)) {
            Assert.assertTrue(locker.isLocked());

            //测试锁在其他线程无法获取锁
            asynccall.submit(this, "otherThread", key);
            //测试其他线程获取另外的锁
            asynccall.submit(this, "otherThread", "test2");
            //获取异步调用返回值
            List<Object> asyncRes = asynccall.get();
            Assert.assertFalse("相同的锁在其他线程无法获取", (boolean) asyncRes.get(0));
            Assert.assertTrue("不同的锁在其他线程获取", (boolean) asyncRes.get(1));

            //测试同一个线程里锁重入
            RedisLocker locker2 = redisLockUtils.lock(key, 5);
            Assert.assertTrue(locker2.isLocked());
            redisLockUtils.unlock(locker2);
        } catch (CatException e) {
            Assert.assertFalse("不应抛出异常", true);
        }

        //自动关闭后能再次获取锁
        RedisLocker locker = redisLockUtils.lock(key, 5);
        Assert.assertTrue(locker.isLocked());
        locker.close();
    }


    @Test
    public void lockWithTime() {

        String key = "test2";

        redisClient.del(RedisLockUtils.REDIS_KEY + key);
        try (RedisLocker locker = redisLockUtils.lockWithTime(key, 5)) {
            Assert.assertTrue(locker.isLocked());
            //没释放前可以重复上锁
            //测试同一个线程里锁重入
            RedisLocker locker2 = redisLockUtils.lock(key, 5);
            Assert.assertTrue(locker2.isLocked());
            redisLockUtils.unlock(locker2);

        } catch (CatException e) {
            Assert.assertFalse("不应抛出异常", true);
        }
        //关闭后无法重现新获得锁
        try (RedisLocker locker = redisLockUtils.lock(key, 5)) {
            Assert.assertFalse(locker.isLocked());
        } catch (CatException e) {
            Assert.assertFalse("不应抛出异常", true);
        }
    }

    public boolean otherThread(String key) {
        try (RedisLocker locker = redisLockUtils.lock(key, 5)) {
            return locker.isLocked();
        } catch (CatException e) {
            Assert.assertTrue("不应抛出异常", false);
            throw e;
        }
    }
}
