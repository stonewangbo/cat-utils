package com.s1coder.cat.error;

/**
 * 类名: RedisClientException <br/>
 * 用途: 业务类型的异常<br/>
 *
 * @author wangbo <br/>
 * Dec 29, 2016 8:00:19 PM
 */
public class RedisClientException extends CatException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public RedisClientException() {
        super();

    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public RedisClientException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

    /**
     * @param message
     * @param cause
     */
    public RedisClientException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param message
     */
    public RedisClientException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public RedisClientException(Throwable cause) {
        super(cause);

    }


}
