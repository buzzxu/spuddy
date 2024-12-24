package io.github.buzzxu.spuddy.redis;


import redis.clients.jedis.Jedis;

import java.util.function.Function;

public interface Redis {

    /**
     * Jedis 原生操作
     * @param func
     * @param <R>
     * @param <SYNC>
     * @return
     */
    <R, SYNC extends Function<Jedis, R>> R execute(SYNC func);





}
