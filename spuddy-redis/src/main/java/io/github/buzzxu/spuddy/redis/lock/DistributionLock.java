package io.github.buzzxu.spuddy.redis.lock;


import io.github.buzzxu.spuddy.redis.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.UUID;

/**
 * @author 徐翔
 * @create 2021-08-25 16:00
 **/
public class DistributionLock {
    private static final Logger logger = LoggerFactory.getLogger(DistributionLock.class);


    private RedisPool redisPool;

    public DistributionLock(RedisPool redisPool) {
        this.redisPool = redisPool;
    }

    /**
     * 加锁
     *
     * @param lockName 锁的key
     * @return 锁标识
     */
    public String lockWithTimeout(String lockName) {
        return this.lockWithTimeout("lock:" + lockName, 8000, 2000);
    }

    /**
     * 加锁
     *
     * @param lockName       锁的key
     * @param acquireTimeout 获取超时时间(单位毫秒)
     * @param timeout        锁的超时时间(单位毫秒)
     * @return 锁标识
     */
    public String lockWithTimeout(String lockName, long acquireTimeout, long timeout) {
        Assert.hasText(lockName, "分布式锁参数异常");
        Jedis conn = null;
        String retIdentifier = null;
        try {
            // 获取连接
            conn = redisPool.getResource();
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = "lock:" + lockName;
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExpire = (int) (timeout / 1000);

            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquireTimeout;
            while (System.currentTimeMillis() < end) {
                if (conn.setnx(lockKey, identifier) == 1) {
                    conn.expire(lockKey, lockExpire);
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
                // 这里重新添加过期时间是因为:由于当前方式下,setNx和expire不具有原子性
                // 可能在极端条件下会造成加了锁却没有添加锁过期时间的情况,这时就会造成死锁
                // 为了避免死锁,这里重新尝试获取过期时间,如果没有过期时间则重新添加过期时间
                if (conn.ttl(lockKey) == -1) {
                    conn.expire(lockKey, lockExpire);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     *
     * @param lockName   锁的key
     * @param identifier 释放锁的标识
     * @return
     */
    public boolean releaseLock(String lockName, String identifier) {
        Jedis conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = redisPool.getResource();
            while (true) {
                // 监视lock，准备开始事务
                conn.watch(lockKey);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }
        } catch (JedisException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retFlag;
    }


}