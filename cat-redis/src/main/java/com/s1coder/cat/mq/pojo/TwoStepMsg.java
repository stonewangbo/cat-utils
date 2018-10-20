package com.s1coder.cat.mq.pojo;


/**
 * 类名: TwoStepMsg <br/>
 * 用途: 两步提交消息 <br/>
 *
 * @author wangbo <br/>
 * Oct 19, 2017 3:10:09 PM
 */
public class TwoStepMsg {
    /**
     * 需发送的队列名
     */
    private String queue;
    /**
     * 消息对象
     */
    private Object msg;


    /**
     *
     */
    public TwoStepMsg() {
        super();
    }

    /**
     * @param queue
     * @param msg
     */
    public TwoStepMsg(Object msg, String queue) {
        super();
        this.queue = queue;
        this.msg = msg;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }


    public String getQueue() {
        return queue;
    }


    public void setQueue(String queue) {
        this.queue = queue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TwoStepMsg [queue=" + queue + ", msg=" + msg + "]";
    }


}
