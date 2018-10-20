package com.s1coder.cat.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.s1coder.cat.error.RedisMqException;
import com.s1coder.cat.error.UtilsParamException;
import com.s1coder.cat.mq.RedisMq;
import com.s1coder.cat.mq.pojo.MqMsg;
import com.s1coder.cat.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 类名: RedisMq <br/>
 * 用途: 使用redis实现的分布式多路MQ消息队列,用于在应用集群内部分发数据 <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 12:02:40 PM
 */
@Service
public class RedisMqService implements RedisMq {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "catRedisClient")
    private RedisClient redisClient;


    public final String keyp = ":mq:";


    @Override
    public void sendMessage(Object obj, String queue) {
        sendMessage(obj, queue, null);
    }


    @Override
    public boolean sendMessage(Object obj, String queue, Long limitSize) {
        checkSendMsg(obj, queue);
        if (limitSize != null) {
            if (limitSize <= 0) {
                throw new UtilsParamException("limitSize:" + limitSize + " 不能小于0");
            }
            long ll = redisClient.llen(keyp + queue);
            if (ll > 0 && ll > limitSize) {
                logger.warn("消息队列:{} 当前大小:{} 大于指定的限制:{} 消息丢弃不发送...", queue, ll, limitSize);
                return false;
            }
        }
        MqMsg msg = new MqMsg(JSONObject.toJSONString(obj), obj.getClass());
        sendMessage(msg, queue);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T receiveMessage(String queue) {
        String channelName = keyp + queue;
        String json = redisClient.brpop(0, channelName);
        if (StringUtils.isEmpty(json)) {
            throw new RedisMqException("队列:" + queue + " 获取内容为空");
        }
        T res = null;
        try {
            MqMsg msg = JSONObject.parseObject(json, MqMsg.class);

            res = (T) JSONObject.parseObject(msg.getMsg(), msg.getType());
            logger.info("读取队列:{} 内容:{}", queue, json);
            return res;
        } catch (Exception e) {
            throw new RedisMqException("读取队列:" + queue + " 解析失败,内容:" + json, e);
        }

    }

    @Override
    public void cleanMessage(String queue) {
        redisClient.del(keyp + queue);
    }

    private void sendMessage(MqMsg msg, String queue) {
        String msgStr = JSONObject.toJSONString(msg);
        long res = redisClient.lpush(keyp + queue, msgStr);
        logger.info("插入队列:{} 队列大小:{} 内容:{}", queue, res, msg);
    }


    public static void checkSendMsg(Object obj, String queue) {
        if (null == obj) {
            throw new UtilsParamException("发送的消息对象obj 不能为空");
        }
        if (null == queue) {
            throw new UtilsParamException("发送的消息类型queue 不能为空");
        }
    }
}
