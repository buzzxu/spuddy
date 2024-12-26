package io.github.buzzxu.spuddy.security.shiro.serializer;



import io.github.buzzxu.spuddy.jackson.Jackson;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.lang.io.SerializationException;

public class ObjectSerializer<V extends AuthorizationInfo> implements RedisSerializer<V> {
    private final Class<V> clazz;
    static {

    }
    public ObjectSerializer(Class<V> clazz){
        this.clazz  = clazz;
    }
    @Override
    public String serialize(V o) throws SerializationException {
        return Jackson.object2Json(o);
    }

    @Override
    public V deserialize(String value) throws SerializationException {
        return Jackson.json2Object(value, clazz);
    }
}
