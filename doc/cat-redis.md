# cat-redis
## 目的
提供基于redis集群的客户端示例
基于redis的分布式可重入锁实现
基于redis的多通道消息列队
## 用途和意义
提供集成化的redis操作,封装了redis支持的绝大部分功能,同时提供了基于redis的锁和消息队列

## 版本更新记录
1.1.7 更新支持lockWithTime 适合定时任务实用的锁类型

## 使用方式
mock 测试工具 [使用说明](../readme.md)


## 使用方式
[redis分布式锁使用示例](../cat-redis/src/test/java/com/s1coder/cat/redis/RedisLockUtilsTest.java)

[redis-mq消息队列和两步提交消息使用示例](../cat-redis/src/test/java/com/s1coder/cat/mq/RedisMqTest.java)

[全局唯一ID生成器,使用示例](../cat-redis/src/test/java/com/s1coder/cat/id/IdGeneratorTest.java)

### 配置示例
[spring配置文件](../cat-redis/src/test/resources/redis-cluster.xml)

[properties配置示例](../cat-redis/src/test/resources/redis.properties)
