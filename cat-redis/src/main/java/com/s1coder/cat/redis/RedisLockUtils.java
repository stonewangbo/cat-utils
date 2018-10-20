package com.s1coder.cat.redis;

import com.s1coder.cat.error.RedisClientException;
import com.s1coder.cat.error.RedisLockException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名: RedisLockUtils <br>
 * 用途: redis全局可重入锁封装 <br>
 * 注意: 在使用可重入锁时,必须保证在调用锁定逻辑后一定成对调用解锁逻辑<br>
 * 否则内存中的可重入标识会造成下次加锁无效!<br>
 * 加锁和解锁操作必须在同一个线程中完成<br>
 * 示范代码:<br>
 * try(RedisLocker locker = redisLockUtils.lock(key, lockTime)){<br>
 * if (!locker.isLocked()) {<br>
 * //加锁失败处理<br>
 * }<br>
 * //业务处理<br>
 * }catch(RedisLockException e){<br>
 * //异常处理<br>
 * }<br>
 *
 * @author wangbo <br>
 * Nov 26, 2016 3:34:48 PM
 */
@Service
public class RedisLockUtils {
    @Resource(name = "catRedisClient")
    private RedisClient redisClient;


    public static final String REDIS_KEY = ":lock:";

    private String getRedisKey() {
        return REDIS_KEY;
    }

    ThreadLocal<Map<String, Integer>> localLock = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };

    /**
     * 建议使用try-with-resource的方式申请锁<br>     *
     *
     * @param id
     * @param seconds
     * @return 返回锁实体, 可用于判断锁是否成功, 以及用来释放锁
     * @throws RedisClientException
     */
    public RedisLocker lock(String id, long seconds) throws RedisLockException, RedisClientException {
        int lockNum = getLockNum(id);
        if (lockNum > 0) {
            //如果本线程已经获得锁,则无需再次获取全局所
            setLockNum(id, lockNum + 1);
            return new RedisLocker(this, id, true);
        } else {
            //没有本地锁的情况下,先尝试获取全局锁
            boolean res = redisClient.setNx(getRedisKey() + id, "lock", seconds);
            if (res) {
                setLockNum(id, 1);
            }
            return new RedisLocker(this, id, res);
        }
    }

    /**
     * 建议使用try-with-resource的方式申请锁<br>
     * 锁释放时只释放本地锁，不主动释放redis锁，适合定时任务互斥使用
     *
     * @param id
     * @param seconds 只有到指定的时间后才由redis释放锁
     * @return
     * @throws RedisLockException
     * @throws RedisClientException
     */
    public RedisLocker lockWithTime(String id, long seconds) throws RedisLockException, RedisClientException {
        RedisLocker res = this.lock(id, seconds);
        res.setWithTime(true);
        return res;
    }

    /**
     * 只有锁定成功时才能解锁
     *
     * @param locker
     * @throws RedisClientException
     * @throws Exception
     */
    public void unlock(RedisLocker locker) throws RedisLockException, RedisClientException {
        if (locker == null || StringUtils.isEmpty(locker.getKey())) {
            throw new RedisLockException("传入的locker无效");
        }
        String id = locker.getKey();
        int lockNum = getLockNum(id);
        if (lockNum == 0) {
            throw new RedisLockException("当前线程并未获得:" + id + " 的锁,不能进行解锁操作");
        }
        //重入锁释放后,并非isWithTime条件，才释放redis全局锁,否则不操作锁
        if (lockNum == 1 && !locker.isWithTime()) {
            redisClient.del(getRedisKey() + id);
        }
        setLockNum(id, lockNum - 1);
    }


    private int getLockNum(String key) {
        Integer res = localLock.get().get(key);
        if (res == null) {
            res = 0;
        }
        return res;
    }

    private void setLockNum(String key, int num) {
        if (num == 0) {
            localLock.get().remove(key);
            //没有数据时主动清除ThreadLocal,防止内存泄露
            if (localLock.get().size() == 0) {
                localLock.remove();
            }
        } else {
            localLock.get().put(key, num);
        }
    }
}
