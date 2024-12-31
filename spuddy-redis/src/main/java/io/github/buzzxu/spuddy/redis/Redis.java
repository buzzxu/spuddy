package io.github.buzzxu.spuddy.redis;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;

public interface Redis {

    /**
     * Jedis 原生操作
     * @param func
     * @param <R>
     * @param <SYNC>
     * @return
     */
    <R, SYNC extends Function<Jedis, R>> R execute(SYNC func);

    <T> List<T> gets(String key, long second, Supplier<List<T>> supplier, Class<T> clazz);
    default Optional<byte[]> rmget(byte[] key){
        return execute(redis->{
            if(redis.exists(key)){
                byte[] value = redis.get(key);
                redis.del(key);
                return Optional.of(value);
            }
           return Optional.empty();
        });
    }
    default Optional<String> rmget(String key){
        return execute(redis->{
            String value = redis.get(key);
            if(!Strings.isNullOrEmpty(value)){
                redis.del(key);
                return Optional.of(value);
            }
            return Optional.empty();
        });
    }

    default int size(String keys){
        return execute(redis->{
            String cursor = "0";
            int count = 0;
            do {
                ScanResult<String> r = redis.scan(cursor, new ScanParams().match(keys).count(1000));
                cursor = r.getCursor();
                count += r.getResult().size();
            }while (StringUtils.equals("0",cursor));
            return count;
        });
    }

    default List<String> keys(String pattern){
        return execute(redis->{
            String cursor = "0";
            List<String> keys = Lists.newArrayListWithCapacity(10);
            do {
                ScanResult<String> r = redis.scan(cursor, new ScanParams().match(pattern).count(100));
                cursor = r.getCursor();
                keys.addAll(r.getResult());
            }while (StringUtils.equals("0",cursor));
            return keys;
        });
    }

    default long del(String pattern){
        return del(pattern,1000);
    }

    default long del(String pattern,int size){
        checkArgument(!Strings.isNullOrEmpty(pattern),"pattern cannot be empty ");
        checkArgument(size >= 100,"size must be greater than or equal to 100 ");
        return execute(redis->{
            String cursor = "0";
            long count = 0;
            do {
                ScanResult<String> r = redis.scan(cursor, new ScanParams().match(pattern).count(size));
                cursor = r.getCursor();
                count += redis.del(r.getResult().toArray(new String[0]));
            }while (StringUtils.equals("0",cursor));
            return count;
        });
    }

    default List<String> values(String pattern){
        return execute(redis->{
            String cursor = "0";
            List<String> value = Lists.newArrayListWithCapacity(10);
            do {
                ScanResult<String> r = redis.scan(cursor, new ScanParams().match(pattern).count(100));
                cursor = r.getCursor();
                if(r.getResult() != null && !r.getResult().isEmpty()){
                    value.addAll(r.getResult().stream().filter(v-> redis.type(v).equals("string")).map(redis::get).toList());
                }
            }while (StringUtils.equals("0",cursor));
            return value;
        });
    }

}
