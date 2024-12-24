package io.github.buzzxu.spuddy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import io.github.buzzxu.spuddy.jackson.Jackson;
import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2019-02-18 21:55:01
 **/
public class Converts {




    /****************************** 类型转换 *******************************/

    public static <T, R> Stream<R> from(Collection<T> from, Function<T, R> func) {
        return from.stream().map(func);
    }
    public static <T, R> Stream<R> fromIterable(Iterable<T> from, Function<T, R> func) {
        return StreamSupport.stream(from.spliterator(),false).map(func);
    }
    public static <T, R> Stream<R> fromArray(T[] from,
                                             Function<T, R> func) {
        return Stream.of(from).map(func);
    }
    public static <T, U> U[] to(T[] from,
                                Function<T, U> func,
                                IntFunction<U[]> generator) {
        return java.util.Arrays.stream(from).map(func).toArray(generator);
    }
    public static <T, R> List<R> list(Collection<T> from, Function<T, R> func) {
        return from(from,func).collect(toList());
    }
    public static <T, R> Set<R> set(Collection<T> from, Function<T, R> func) {
        return from(from,func).collect(toSet());
    }


    /****************************** Bean/Map互转 *******************************/



    /**
     * 将对象装换为map
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> bean2Map(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key.toString(), beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     * @param map
     * @param bean
     * @return
     */
    public static <T> T map2Bean(Map<String, Object> map,T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将List{T}转换为List{Map{String, Object}}
     * @param objList
     * @return
     */
    public static <T> List<Map<String, Object>> objects2Maps(List<T> objList) {
        List<Map<String, Object>> list = Lists.newArrayListWithCapacity(objList.size());
        if (objList != null && !objList.isEmpty()) {
            for (int i = 0,size = objList.size(); i < size; i++) {
                T bean = objList.get(i);
                list.add(bean2Map(bean));
            }
        }
        return list;
    }

    /**
     * 将List{Map{String,Object}}转换为{T}
     * @param maps
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> maps2Objects(List<Map<String, Object>> maps,Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<T> list = Lists.newArrayListWithCapacity(maps.size());
        if (maps != null && !maps.isEmpty()) {
            for (int i = 0,size = maps.size(); i < size; i++) {
                Map<String, Object> map = maps.get(i);
                T bean = clazz.getDeclaredConstructor().newInstance();
                map2Bean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * 对象 转 <code>map{string,string}</code>
     * @param obj
     * @return
     */
    public static Map<String, String> map(Object obj) {
        Map<String, JsonNode> intermediateMap = Jackson.of().convertValue(obj, new TypeReference<Map<String, JsonNode>>() {});
        Map<String, String> finalMap = Maps.newHashMapWithExpectedSize(intermediateMap.size() + 1);// Start out big enough to prevent resizing
        for (Map.Entry<String, JsonNode> e : intermediateMap.entrySet()) {
            String key = e.getKey();
            JsonNode val = e.getValue();
            String stringVal;
            if(val.isNumber()){
                switch (val.numberType()) {
                    case BIG_INTEGER:
                        stringVal = val.bigIntegerValue().toString();
                        break;
                    case BIG_DECIMAL:
                        stringVal = val.decimalValue().toPlainString();
                        break;
                    case INT:
                    case LONG:
                        stringVal = String.valueOf(val.asLong());
                        break;
                    case FLOAT:
                    case DOUBLE:
                        stringVal = Double.toString(val.asDouble());
                        break;
                    default:
                        stringVal = val.isTextual() ? val.textValue() : val.toString();
                }
            }else{
                stringVal = val.isTextual() ? val.textValue() : val.toString();
            }
            finalMap.put(key, stringVal);
        }
        return finalMap;
    }
    /**
     * <code>map{string,string}</code>  转 对象
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T bean(Map<String, String> map, Class<T> clazz) {
        Map<String, JsonNode> intermediateMap = Maps.newHashMapWithExpectedSize(map.size() + 1);
        try {
            for (Map.Entry<String, String> e : map.entrySet()) {
                String key = e.getKey();
                String val = e.getValue();
                JsonNode jsonVal;
                if (val.startsWith("{") || val.startsWith("[") || "null".equals(val)) {
                    jsonVal = Jackson.of().readValue(val, JsonNode.class);
                } else {
                    jsonVal = Jackson.of().convertValue(val, JsonNode.class);
                }
                intermediateMap.put(key, jsonVal);
            }
            T result = Jackson.of().convertValue(intermediateMap, clazz);
            return result;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


    public static <T> T convert(Class<T> type, Object value)  {
        return convert((java.lang.reflect.Type)type, value);
    }
    public static <T> T convert(java.lang.reflect.Type type, Object value) {
        return convert(type, value,null);
    }

    public static <T> T convert(java.lang.reflect.Type type, Object value, T defaultValue)  {
        return convertWithCheck(type, value, defaultValue, false);
    }

    public static <T> T convert(Object value)  {
        return convertWithCheck(value,null,false);
    }
    public static <T> T convert(Object value, T defaultValue)  {
        return convertWithCheck(value,defaultValue,false);
    }
    public static <T> T convertWithCheck(Object value, T defaultValue, boolean quietly)  {
        if(value == null){
            return defaultValue;
        }
        try {
            TypeToken<?> typeToken = TypeToken.of(value.getClass());
            return (T) typeToken.getRawType().cast(value);
        }catch (Exception ex){
            if (quietly) {
                return defaultValue;
            } else {
                throw ex;
            }
        }
    }

    public static <T> T convertWithCheck(java.lang.reflect.Type type, Object value, T defaultValue, boolean quietly) {
        if(value == null){
            return defaultValue;
        }
        TypeToken<?> typeToken = TypeToken.of(type);
        try {
            return (T) typeToken.getRawType().cast(value);
        }catch (Exception ex){
            if (quietly) {
                return defaultValue;
            } else {
                throw ex;
            }
        }
    }

}
