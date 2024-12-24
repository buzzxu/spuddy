package io.github.buzzxu.spuddy.redis;



import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.util.function.Function;

/**
 * @program:
 * @description:
 * @author: xux
 * @created: 2021/04/26 16:06
 */
abstract class AbstractRedis implements Redis, Closeable {

    @Override
    public <R, SYNC extends Function<Jedis, R>> R execute(SYNC func) {
        try( Jedis jedis = getResource()){
            return func.apply(jedis);
        }catch (IllegalArgumentException | ApplicationException e){
            throw e;
        } catch (Exception e){
            throw ApplicationException.raise(e.getMessage(),e);
        }
    }

    public abstract Jedis getResource();
}
