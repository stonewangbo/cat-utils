package com.s1coder.cat.error;


/**
 * 类名: UtilsParamException <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Dec 26, 2017 4:02:36 PM
 */
public class UtilsParamException extends CatException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8562206554572723949L;

    /**
     *
     */
    public UtilsParamException() {
        super();

    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public UtilsParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

    /**
     * @param message
     * @param cause
     */
    public UtilsParamException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param message
     */
    public UtilsParamException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public UtilsParamException(Throwable cause) {
        super(cause);

    }

}
