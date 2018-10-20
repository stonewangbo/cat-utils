package com.s1coder.cat.mq.impl;

import com.s1coder.cat.mq.RedisMq;
import com.s1coder.cat.mq.TwoStepMq;
import com.s1coder.cat.mq.pojo.TwoStepMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 类名: TwoStepMqMsgService <br/>
 * 用途: 两步提交消息实现 <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 1:27:57 PM
 */
@Service
public class TwoStepMqMsgService implements TwoStepMq {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisMq redisMq;

    /**
     * 线程共享变量,用于保存在事务中提交的发送请求
     */
    private ThreadLocal<List<TwoStepMsg>> needSendId = new ThreadLocal<List<TwoStepMsg>>() {

        @Override
        protected List<TwoStepMsg> initialValue() {
            return new ArrayList<>();
        }
    };

    @Override
    public void twoStepSendFirst(Object obj, String queue) {
        RedisMqService.checkSendMsg(obj, queue);
        needSendId.get().add(new TwoStepMsg(obj, queue));
    }

    @Override
    public void twoStepSendSecond() {
        for (TwoStepMsg msg : needSendId.get()) {
            redisMq.sendMessage(msg.getMsg(), msg.getQueue(), null);
        }
        needSendId.remove();
    }

}
