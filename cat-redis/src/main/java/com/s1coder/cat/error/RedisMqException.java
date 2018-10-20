package com.s1coder.cat.error;


/**
 * 类名: RedisMqException <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Feb 13, 2018 1:06:45 PM
 */
public class RedisMqException extends CatException {

    /**
     *
     */
    public RedisMqException() {

    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public RedisMqException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

    /**
     * @param message
     * @param cause
     */
    public RedisMqException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param message
     */
    public RedisMqException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public RedisMqException(Throwable cause) {
        super(cause);

    }

}
