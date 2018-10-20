package com.s1coder.cat.id;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author wangbo
 * @version 1.0
 * @description 测试
 * @date 2018/10/8 2:56 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-init-test.xml"
})
@Ignore
public class IdGeneratorTest {
    @Resource
    private IdGenerator idGenerator;

    @Test
    public void generatorId() {
        //生成数字类型id,长度不会超过18位
        long id = idGenerator.generatorId("test_table");
        long id2 = idGenerator.generatorId("test_table");
        //几乎同时生成的两个ID,不会相同
        Assert.assertNotEquals(id, id2);
    }
}
