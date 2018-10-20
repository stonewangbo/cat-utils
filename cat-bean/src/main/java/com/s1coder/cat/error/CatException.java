package com.s1coder.cat.error;


/**
 * 类名: CatException <br/>
 * 用途: 基础异常类 <br/>
 *
 * @author wangbo <br/>
 * Dec 26, 2017 4:01:23 PM
 */
public class CatException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7073620018464781855L;

    /**
     *
     */
    public CatException() {
        super();

    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public CatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

    /**
     * @param message
     * @param cause
     */
    public CatException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param message
     */
    public CatException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public CatException(Throwable cause) {
        super(cause);

    }

}
