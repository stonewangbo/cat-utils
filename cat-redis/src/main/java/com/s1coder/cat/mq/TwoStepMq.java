package com.s1coder.cat.mq;


/**
 * 类名: TwoStepMqMsg <br/>
 * 用途: 两步消息提交,消息基于RedisMq<br>
 * 仅用于两步消息发送,消息接收仍使用RedisMq.receiveMessage <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 1:27:01 PM
 */
public interface TwoStepMq {
    /**
     * 两步消息提交,第一步:提交消息到线程缓存中
     *
     * @param obj   消息体
     * @param queue 队列名称
     */
    void twoStepSendFirst(Object obj, String queue);

    /**
     * 步消息提交,第二步:将本线程缓存的消息全部发送
     * 需要和twoStepSendFirst后同一个线程内调用
     */
    public void twoStepSendSecond();
}
