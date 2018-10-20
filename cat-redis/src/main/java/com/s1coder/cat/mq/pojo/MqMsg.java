package com.s1coder.cat.mq.pojo;


/**
 * 类名: MqMsg <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 12:49:25 PM
 */
public class MqMsg {
    /**
     * 数据内容
     **/
    private String msg;

    /**
     * 消息对象类型
     */
    private Class<?> type;

    /**
     *
     */
    public MqMsg() {
        super();
    }


    /**
     * @param msg
     * @param type
     */
    public MqMsg(String msg, Class<?> type) {
        super();
        this.msg = msg;
        this.type = type;
    }


    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }


    public Class<?> getType() {
        return type;
    }


    public void setType(Class<?> type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MqMsg [msg=" + msg + ", type=" + type + "]";
    }


}
