package io.github.buzzxu.spuddy.util.id;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.buzzxu.spuddy.util.Random.numeric;


/**
 * 简单的ID生成
 * @author xux
 * @date 2018/6/25 下午2:23
 */
public class SimpleIdWorkder {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("YYMMddHHmmss");
    private static final Cache<String, byte[]> CACHE_ID = Caffeine.newBuilder()
            .maximumSize(10_000)
            //默认1分钟超时
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();

    private static final ReentrantLock lock = new ReentrantLock();


    public static String nextId(int count){
        lock.lock();
        try {
            return id(count);
        }finally {
            lock.unlock();
        }
    }


    private static String id(int count){
        String id = LocalDateTime.now().format(DTF) + numeric(count);
        if(CACHE_ID.getIfPresent(id) == null){
            CACHE_ID.put(id,new byte[0]);
            return id;
        }
        return id(count);
    }
}
