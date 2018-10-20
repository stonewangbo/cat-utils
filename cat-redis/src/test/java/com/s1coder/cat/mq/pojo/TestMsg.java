package com.s1coder.cat.mq.pojo;


/**
 * 类名: TestMsg <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 2:19:02 PM
 */
public class TestMsg {
    private String text;

    /**
     * @param text
     */
    public TestMsg(String text) {
        super();
        this.text = text;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


}
