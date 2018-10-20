package bean;

import com.alibaba.fastjson.JSONObject;
import com.s1coder.cat.bean.BeanInitializer;
import com.s1coder.cat.bean.PojoCopyUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PojoCopyUtilTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    class Test1 {
        public Test1(String p1, String p2) {
            super();
            this.p1 = p1;
            this.p2 = p2;
        }

        String p1;
        String p2;

        @Override
        public String toString() {
            return "Test1 [p1=" + p1 + ", p2=" + p2 + "]";
        }


    }

    class Test2 {
        public Test2(String p1, byte[] p2) {
            super();
            this.p1 = p1;
            this.p2 = p2;
        }

        String p1;
        byte[] p2;

        @Override
        public String toString() {
            return "Test1 [p1=" + p1 + ", p2=" + p2 + "]";
        }


    }

    @Test
    public void test() {
        Test1 t1 = new Test1(null, "123");
        try {
            PojoCopyUtil.checkNull(t1, Arrays.asList("p1"));
            Assert.assertTrue("没有异常", true);
        } catch (Exception e) {
            Assert.assertTrue("不应抛出异常异常", false);
        }

        try {
            PojoCopyUtil.checkNull(t1);
            Assert.assertTrue("应抛出异常", false);
        } catch (Exception e) {
            Assert.assertTrue("异常", true);
        }

        Test1 t2 = new Test1(null, null);
        PojoCopyUtil.copy(t1, t2);
        logger.info("t1:{}", t1);
        logger.info("t2:{}", t2);

        Test2 t22 = new Test2(null, null);
        PojoCopyUtil.copy(t1, t22);
        logger.info("t1:{} t22:{}", t1.p2, new String(t22.p2));
        Assert.assertEquals(t1.p2, new String(t22.p2));

        t22.p2 = "test2".getBytes();
        t1.p2 = null;
        PojoCopyUtil.copy(t22, t1);
        logger.info("t22:{} t1:{}", new String(t22.p2), t1.p2);
        Assert.assertEquals(t1.p2, new String(t22.p2));
    }

    @Test
    public void test2() {
        SapmlePojoExt ext = new SapmlePojoExt();
        BeanInitializer.init(ext);

        ext.setLockId("");

        ext.setStatus(S1UseStatus.REG);


        SamplePojo record = new SamplePojo();
        PojoCopyUtil.copy(ext, record);
        logger.info("ext:{}", JSONObject.toJSONString(ext));
        logger.info("record:{}", JSONObject.toJSONString(record));
    }
}
