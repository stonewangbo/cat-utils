package bean;


/**
 * 类名: S1UseStatus <br/>
 * *
 *
 * @author wangbo <br/>
 * Mar 22, 2018 6:06:04 PM
 */
public enum S1UseStatus {
    UNREG("没有鹅"),
    REG("鹅已注册");


    final String desc;

    S1UseStatus(String desc) {
        this.desc = desc;
    }
}
