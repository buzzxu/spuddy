package io.github.buzzxu.spuddy.security.shiro.serializer;


import org.apache.shiro.lang.io.SerializationException;

public interface RedisSerializer<T> {

    String serialize(T t) throws SerializationException;

    T deserialize(String value) throws SerializationException;
}
