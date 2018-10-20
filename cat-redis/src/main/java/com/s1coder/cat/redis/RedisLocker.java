package com.s1coder.cat.redis;

import com.s1coder.cat.error.RedisClientException;
import com.s1coder.cat.error.RedisLockException;

/**
 * 类名: RedisLocker <br/>
 * 用途: 锁实体,可自动关闭 <br/>
 *
 * @author wangbo <br/>
 * Mar 28, 2017 3:30:24 PM
 */
public class RedisLocker implements AutoCloseable {
    private RedisLockUtils redisLockUtils;

    private String key;


    boolean locked;

    /**
     * 是否按时间进行锁定，配置此条件后，锁释放时只释放本地锁，不主动释放redis锁，适合定时任务互斥使用
     */
    boolean withTime = false;

    /**
     * @param redisLockUtils
     * @param key
     * @param locked
     */
    public RedisLocker(RedisLockUtils redisLockUtils, String key, boolean locked) {
        super();
        this.redisLockUtils = redisLockUtils;
        this.key = key;
        this.locked = locked;
    }

    public String getKey() {
        return key;
    }

    public boolean isLocked() {
        return locked;
    }


    public void setWithTime(boolean withTime) {
        this.withTime = withTime;
    }

    public boolean isWithTime() {
        return withTime;
    }


    @Override
    public void close() throws RedisLockException, RedisClientException {
        if (locked) {
            redisLockUtils.unlock(this);
        }
    }

}
