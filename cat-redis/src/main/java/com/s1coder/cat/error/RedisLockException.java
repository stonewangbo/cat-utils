package com.s1coder.cat.error;

/**
 * 类名: BussException <br/>
 * 用途: 业务类型的异常<br/>
 *
 * @author wangbo <br/>
 * Dec 29, 2016 8:00:19 PM
 */
public class RedisLockException extends CatException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4885966154899125472L;

    /**
     *
     */
    public RedisLockException() {
        super();

    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public RedisLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

    /**
     * @param message
     * @param cause
     */
    public RedisLockException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public RedisLockException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public RedisLockException(Throwable cause) {
        super(cause);

    }


}
