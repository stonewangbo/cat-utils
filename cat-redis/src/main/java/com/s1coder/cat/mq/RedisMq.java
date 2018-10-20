package com.s1coder.cat.mq;

/**
 * 类名: RedisMq <br/>
 * 用途: 使用redis实现的分布式多路MQ消息队列,用于在应用集群内部分发数据  <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 1:19:20 PM
 */
public interface RedisMq {
    /**
     * 发送消息
     *
     * @param obj   消息体
     * @param queue 使用队列名称
     */
    void sendMessage(Object obj, String queue);

    /**
     * @param obj       消息体
     * @param queue     使用队列名称
     * @param limitSize 限制队列大小
     * @return true:成功发送 false:达到限制队列大小,消息未发成功
     */
    boolean sendMessage(Object obj, String queue, Long limitSize);

    /**
     * 接收消息
     *
     * @param queue 使用队列名称
     * @return 返回的消息体
     */
    <T> T receiveMessage(String queue);


    /**
     * 清空队列中所有消息
     *
     * @param queue
     */
    void cleanMessage(String queue);

}
