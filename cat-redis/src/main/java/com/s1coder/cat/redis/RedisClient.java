package com.s1coder.cat.redis;

import com.s1coder.cat.error.RedisClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;

/**
 * 类名: RedisClient <br/>
 * 用途: 隔离redis客户端开源包,redis集群版本 <br/>
 *
 * @author wangbo, shiyuanfei <br/>
 * 2015年6月14日 下午4:41:48
 */
public class RedisClient {

    private static final Logger LOG = LoggerFactory.getLogger(RedisClient.class);

    // 返回值
    private static final String RESULT_OK = "ok";
    private static final String RESULT_NIL = "nil";
    // 操作参数
    public static final String LIST_WHERE_BEFORE = "before";
    public static final String LIST_WHERE_AFTER = "after";

    /**
     * 按环境区分的redis key前缀
     */
    private String keyPre;

    private JedisCluster jedisCluster;


    /**
     *
     */
    public RedisClient() {
        super();
    }

    /**
     * @param keyPre
     * @param jedisCluster
     */
    public RedisClient(String keyPre, JedisCluster jedisCluster) {
        super();
        this.keyPre = keyPre;
        this.jedisCluster = jedisCluster;
    }

    private <T> T execute(RedisAction<T> action) throws RedisClientException {
        //Jedis jedis = null;
        // jedis.ping()
        try {
            return action.doInJedis(jedisCluster);
        } catch (Exception e) {
            LOG.error("调用redis客户端时出错", e);
            throw new RedisClientException("调用redis客户端时出错", e);
        } finally {
            //redisHolder.returnJedis(jedis);
        }
    }

    /**
     * 类名: RedisAction <br/>
     * 用途: 模仿方法通过redisaction的doinjedis执行实际的redis操作<br/>
     *
     * @author hanjw <br/>
     * 2014年9月26日 下午6:34:44
     */
    private interface RedisAction<V> {

        V doInJedis(JedisCluster jedis) throws RedisClientException;
    }

    // ========================key=============================================

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     * @throws RedisClientException
     */
    public boolean exists(final String key) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return jedis.exists(keyPre + key);
            }
        });
    }

    /**
     * 删除key
     *
     * @param key
     * @return
     * @throws RedisClientException
     */
    public boolean del(final String key) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return jedis.del(keyPre + key) >= 1;
            }
        });
    }


    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key     键名
     * @param seconds 多少秒后自动过期
     * @return 成功还是失败
     * @throws RedisClientException
     */
    public boolean expire(final String key, final int seconds) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return jedis.expire(keyPre + key, seconds) == 1;
            }
        });
    }

    /**
     * 返回 key 所储存的值的类型。
     *
     * @param key key名称
     * @return 类型名称
     * @throws RedisClientException
     */
    public String type(final String key) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                return jedis.type(keyPre + key);
            }
        });
    }

    // ========================key=============================================

    // ========================String==========================================
    public boolean setNx(final String key, final String value, final long time) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                if (time > 0) {
                    return RESULT_OK.equalsIgnoreCase(jedis.set(keyPre + key, value, "NX", "EX", time));
                } else {
                    return jedis.setnx(keyPre + key, value) == 1;
                }
            }
        });
    }

    public boolean setEx(final String key, final String value, final long time) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) throws RedisClientException {
                if (time <= 0) {
                    throw new RedisClientException("time:" + time + " 无效");
                }
                return RESULT_OK.equalsIgnoreCase(jedis.setex(keyPre + key, (int) time, value));
            }
        });
    }

    public boolean set(final String key, final String value) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return RESULT_OK.equals(jedis.set(keyPre + key, value));
            }
        });
    }

    public boolean set(final String key, final byte[] value) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return RESULT_OK.equals(jedis.set((keyPre + key).getBytes(), value));
            }
        });
    }

    public String get(final String key) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                String res = jedis.get(keyPre + key);
                return RESULT_NIL.equals(res) ? null : res;
            }
        });
    }

    public byte[] get(final byte[] key) throws RedisClientException {
        return execute(new RedisAction<byte[]>() {

            @Override
            public byte[] doInJedis(JedisCluster jedis) {
                byte[] res = jedis.get((keyPre + key.toString()).getBytes());
                return RESULT_NIL.equals(new String(res)) ? null : res;
            }
        });
    }

    /**
     * incr:(缓存值自增1). <br/>
     *
     * @param key
     * @return 返回增1后的值 Long
     * @throws RedisClientException
     * @throws
     * @author fiona 当redis中对应key的value值不为整形时，抛异常
     * 当redis中对应key的value值不存在时，默认初始为‘0’，首次使用incr方法返回1
     * 当redis中对应key的value值为整形时,返回增1后的结果
     */
    public Long incr(final String key) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                Long value = jedis.incr(keyPre + key);
                return value;
            }
        });
    }

    /**
     * decr
     * 缓存自动减少1
     *
     * @throws RedisClientException
     */
    public Long decr(final String key) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                Long value = jedis.decr(keyPre + key);
                return value;
            }
        });
    }


    // ========================String==========================================

    // ========================List============================================

    /**
     * 插入头部
     *
     * @param key
     * @param value
     * @return
     * @throws RedisClientException
     */
    public long lpush(final String key, final String value) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                return jedis.lpush(keyPre + key, value);
            }
        });
    }

    public long lpush(final String key, final String... values) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                return jedis.lpush(keyPre + key, values);
            }
        });
    }

    /**
     * 插入尾部
     *
     * @param key
     * @param value
     * @return
     * @throws RedisClientException
     */
    public void rpush(final String key, final String value) throws RedisClientException {
        execute(new RedisAction<Object>() {

            @Override
            public Object doInJedis(JedisCluster jedis) {
                jedis.rpush(keyPre + key, value);
                return null;
            }
        });
    }

    public long rpush(final String key, final String... values) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                return jedis.rpush(keyPre + key, values);
            }
        });
    }

    /**
     * 把新值插入到指定值的前/后面。
     *
     * @param key   列表名
     * @param where 前/后
     * @param pivot 指定值
     * @param value 新值
     * @return 成功:list长度 没有找到pivot:-1 key空:0
     * @throws RedisClientException
     */
    public long linsert(final String key, final String where, final String pivot, final String value) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                BinaryClient.LIST_POSITION position = null;
                if (LIST_WHERE_AFTER.equals(where)) {
                    position = BinaryClient.LIST_POSITION.AFTER;
                } else if (LIST_WHERE_BEFORE.equals(where)) {
                    position = BinaryClient.LIST_POSITION.BEFORE;
                } else {
                    throw new RuntimeException("向list指定值附近插入新值时没有指定具体位置");
                }
                return jedis.linsert(keyPre + key, position, pivot, value);
            }
        });
    }


    public String lpop(final String key) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                String result = jedis.lpop(keyPre + key);
                return RESULT_NIL.equals(result) ? null : result;
            }
        });
    }

    /**
     * 获取尾部元素
     *
     * @param timeout 阻塞时间
     * @param keys    元素所在队列
     * @return
     * @throws RedisClientException
     */
    public String brpop(final int timeout, final String keys) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                List<String> result = jedis.brpop(timeout, keyPre + keys);
                if (result == null || result.isEmpty()) {
                    return null;
                } else {
                    return result.get(1);
                }
            }
        });
    }

    public String rpop(final String key) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                String result = jedis.rpop(keyPre + key);
                return RESULT_NIL.equals(result) ? null : result;
            }
        });
    }

    public String rpoplpush(final String srckey, final String dstkey) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                String result = jedis.rpoplpush(keyPre + srckey, keyPre + dstkey);
                return RESULT_NIL.equals(result) ? null : result;
            }
        });
    }

    public String brpoplpush(final int timeout, final String srckey, final String dstkey) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                String result = jedis.brpoplpush(keyPre + srckey, keyPre + dstkey, timeout);
                return RESULT_NIL.equals(result) ? null : result;
            }
        });
    }

    public String lindex(final String key, final long index) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                String result = jedis.lindex(keyPre + key, index);
                return RESULT_NIL.equals(result) ? null : result;
            }
        });
    }

    /**
     * 计算list长度
     *
     * @param key
     * @return
     * @throws RedisClientException
     */
    public long llen(final String key) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                return jedis.llen(keyPre + key);
            }
        });
    }

    /**
     * 获取指定偏移位置的值
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws RedisClientException
     */
    public List<String> lrange(final String key, final long start, final long end) throws RedisClientException {
        return execute(new RedisAction<List<String>>() {

            @Override
            public List<String> doInJedis(JedisCluster jedis) {
                return jedis.lrange(keyPre + key, start, end);
            }
        });
    }

    /**
     * 删除指定偏移范围内的值
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws RedisClientException
     */
    public boolean ltrim(final String key, final long start, final long end) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return RESULT_OK.equals(jedis.ltrim(keyPre + key, start, end));
            }
        });
    }

    // ========================List============================================

    // ========================Hash============================================
    public void hset(final String key, final String field, final String value) throws RedisClientException {
        execute(new RedisAction<Object>() {

            @Override
            public Object doInJedis(JedisCluster jedis) {
                jedis.hset(keyPre + key, field, value);
                return null;
            }
        });
    }

    public boolean hsetnx(final String key, final String field, final String value) throws RedisClientException {
        return execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return jedis.hsetnx(keyPre + key, field, value) == 1;
            }
        });
    }

    public String hget(final String key, final String field) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                String result = jedis.hget(keyPre + key, field);
                return RESULT_NIL.equals(result) ? null : result;
            }
        });
    }

    // ========================Hash============================================

    // ========================SortedSet=======================================
    // ========================SortedSet=======================================

    // ========================Map=======================================

    /**
     * hmset:(所以键为缓存为key的map). <br/>
     *
     * @param key
     * @param map void
     * @throws RedisClientException
     * @throws
     * @author Administrator
     */
    public void hmset(final String key, final Map<String, String> map) throws RedisClientException {
        execute(new RedisAction<Boolean>() {

            @Override
            public Boolean doInJedis(JedisCluster jedis) {
                return "OK".equals(jedis.hmset(keyPre + key, map));
            }
        });
    }

    /**
     * hdel:(删除缓存中key的map). <br/>
     *
     * @param key void
     * @throws RedisClientException
     * @throws
     * @author Administrator
     */
    public long hdel(final String key) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                return jedis.hdel(keyPre + key);
            }
        });
    }

    /**
     * hgetAll:(获取缓存中为key的map). <br/>
     *
     * @param key
     * @return Map
     * @throws RedisClientException
     * @throws
     * @author Administrator
     */
    public Map<String, String> hgetAll(final String key) throws RedisClientException {
        return execute(new RedisAction<Map<String, String>>() {

            @Override
            public Map<String, String> doInJedis(JedisCluster jedis) {
                return jedis.hgetAll(keyPre + key);
            }
        });
    }

    /**
     * 删除map中某个value
     *
     * @throws RedisClientException
     */
    public long hdelone(final String key, final String field) throws RedisClientException {
        return execute(new RedisAction<Long>() {

            @Override
            public Long doInJedis(JedisCluster jedis) {
                return jedis.hdel(keyPre + key, field);
            }
        });
    }

    /**
     * 获取map中某个name值
     *
     * @throws RedisClientException
     */
    public String hmgetone(final String key, final String name) throws RedisClientException {
        return execute(new RedisAction<String>() {

            @Override
            public String doInJedis(JedisCluster jedis) {
                return jedis.hget(keyPre + key, name);
            }
        });
    }
}