package com.s1coder.cat.id;


import com.s1coder.cat.redis.RedisClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @author wangbo
 * @version 1.0
 * @description 主键生成服务
 * @date 2018/9/30 3:29 PM
 */
@Component
public class IdGenerator {
    @Resource(name = "catRedisClient")
    private RedisClient redisClient;

    /**
     * 随机数生成器,可共用
     */
    private Random random = new Random();


    /**
     * 生成唯一的ID,每个表唯一即可<br>
     * 注意:id长度不会超过18位且有序,由数字类型时间戳+3位随机数组成<br>
     * 分布式应用使用时,注意节点之间本地系统时间需做同步,假如节点之间系统时差超过24小时,则可能会导致生成的id重复
     *
     * @param tableName 表名,不区分大小写
     * @return 返回生成的主键
     */
    public long generatorId(String tableName) {
        long id;
        //时间戳+3位随机数
        do {
            id = Long.parseLong(String.format("%s%03d", System.currentTimeMillis(), random.nextInt(999)));
        } while (!redisClient.setNx(String.format("id:%s:%s", tableName.toLowerCase(), id), "id", 1000 * 60 * 24));

        return id;
    }
}
