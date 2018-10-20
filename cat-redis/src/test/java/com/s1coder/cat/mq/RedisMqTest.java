package com.s1coder.cat.mq;

import com.s1coder.cat.mock.CatMock;
import com.s1coder.cat.mq.impl.RedisMqService;
import com.s1coder.cat.mq.pojo.TestMsg;
import com.s1coder.cat.redis.RedisClient;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类名: RedisMqTest <br/>
 * 用途: 功能测试,连接测试环境redis集群 <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 2:13:09 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-init-test.xml"
})
@Ignore
public class RedisMqTest {
    private CatMock catMock = new CatMock();

    @Autowired
    private RedisMqService redisMq;

    @Autowired
    private TwoStepMq twoStepMq;

    @Autowired
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
    public void mqtest() throws Exception {
        //发送消息
        String str = "tst msg";
        String queue = "tstqueue";
        TestMsg msg = new TestMsg(str);
        //开始测试前需清空队列
        redisMq.cleanMessage(queue);

        redisMq.sendMessage(msg, queue);

        //测试读取消息
        TestMsg res = redisMq.receiveMessage(queue);
        Assert.assertEquals(str, res.getText());

        //测试消息发送限制
        Assert.assertTrue(redisMq.sendMessage(msg, queue, 1l));
        Assert.assertTrue(redisMq.sendMessage(msg, queue, 1l));
        Assert.assertFalse(redisMq.sendMessage(msg, queue, 1l));
    }

    @Test
    public void twoStepTest() throws Exception {
        //两步消息测试
        String queue = "test_twostepQueue";
        //开始测试前需清空队列
        redisMq.cleanMessage(queue);

        TestMsg msg = new TestMsg("two test");
        //两步消息第一步提交
        twoStepMq.twoStepSendFirst(msg, queue);

        //验证这时候队列中还没有数据
        Assert.assertEquals(0, redisClient.llen(redisMq.keyp + queue));

        //两步消息提交
        twoStepMq.twoStepSendSecond();
        //提交后队列中将有数据
        Assert.assertEquals(1, redisClient.llen(redisMq.keyp + queue));

        //提交后即可获取消息内容
        TestMsg res = redisMq.receiveMessage(queue);
        Assert.assertEquals(msg.getText(), res.getText());
    }

}
