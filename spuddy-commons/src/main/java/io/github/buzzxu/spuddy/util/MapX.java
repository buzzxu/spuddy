package io.github.buzzxu.spuddy.util;

import com.google.common.primitives.Ints;
import io.github.buzzxu.spuddy.jackson.Jackson;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by xux on 2017/4/14.
 */
public class MapX<K, V> extends HashMap<K, V> {


    private static final long serialVersionUID = 4530913657960852205L;

    private MapX() {
        super();
    }

    private MapX(int expectedSize, K key, V value) {
        this(expectedSize);
        super.put(key, value);
    }

    private MapX(int expectedSize) {
        super(capacity(expectedSize));
    }

    private MapX(Map<K, V> map) {
        super(map);
    }


    public static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            return expectedSize + 1;
        }
        if (expectedSize < Ints.MAX_POWER_OF_TWO) {
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE;
    }

    public static <K, V> MapX<K, V> of() {
        return new MapX<>();
    }

    public static <K, V> MapX<K, V> of(K key, V value) {
        return new MapX<>(3, key, value);
    }

    public static <K, V> MapX<K, V> of(int expectedSize) {
        return new MapX<>(expectedSize);
    }

    public static <K, V> MapX<K, V> of(Map<K, V> map) {
        return new MapX<>(map);
    }
    @SuppressWarnings("rawtypes")
    public MapX set(K key, V value) {
        super.put(key, value);
        return this;
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    public MapX set(MapX mapX) {
        super.putAll(mapX);
        return this;
    }
    @SuppressWarnings("rawtypes")
    public Map delete(K key) {
        super.remove(key);
        return this;
    }

    public boolean isNull(K key) {
        return get(key) == null;
    }

    public String toJson() {
        return Jackson.object2Json(this);
    }

    @SuppressWarnings("rawtypes")
    public boolean equals(MapX mapX) {
        return mapX instanceof MapX && super.equals(mapX);
    }


}
