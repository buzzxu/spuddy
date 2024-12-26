package io.github.buzzxu.spuddy.security.shiro.cache;


import io.github.buzzxu.spuddy.db.ObjID;
import io.github.buzzxu.spuddy.objects.Id;
import io.github.buzzxu.spuddy.security.shiro.serializer.RedisSerializer;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.subject.PrincipalCollection;

import java.security.Principal;

/**
 * @author xux
 * @date 2018/6/12 上午11:16
 */
abstract class AbstractShiroCache<K,V > implements Cache<K,V> {

    protected final String keyPrefix;
    protected final long expire ;
    protected final RedisSerializer<V> valueSerializer;

    @SuppressWarnings({"unchecked","rawtypes"})
    protected AbstractShiroCache( RedisSerializer valueSerializer,String keyPrefix,long expire ) {
        this.valueSerializer = valueSerializer;
        this.keyPrefix = keyPrefix;
        this.expire = expire;
    }
    @SuppressWarnings("rawtypes")
    protected String hashKey(K key) {
        if(key instanceof PrincipalCollection){
            PrincipalCollection pc=(PrincipalCollection) key;
            return key(pc.getPrimaryPrincipal());
        }
        return key(key);
    }

    private String key(Object id){
        if (id instanceof Principal) {
            return this.keyPrefix +  ((Principal) id).getName();
        }
        if (id instanceof ObjID) {
            return this.keyPrefix +  ((ObjID) id).getId();
        }
        if (id instanceof Id) {
            return this.keyPrefix +  ((Id) id).getId();
        }
        return id.toString();
    }
}
