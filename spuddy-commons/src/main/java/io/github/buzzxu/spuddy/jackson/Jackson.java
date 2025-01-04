package io.github.buzzxu.spuddy.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.jackson.deserializer.EscapeHtmlDeserializer;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2019-08-11 22:33
 **/
public enum Jackson implements JSON{
    INSTANCE{
        private final ObjectMapper instance;
        {
            instance = newObjectMapper();
        }

        private  ObjectMapper newObjectMapper() {
            final ObjectMapper mapper = new ObjectMapper();
            return configure(mapper);
        }


        private  ObjectMapper configure(ObjectMapper mapper) {
            SimpleModule module = new SimpleModule();
//            module.addSerializer(String.class,new ConvertSerializer());
            mapper.registerModule(module);
            mapper.registerModule(new ParameterNamesModule());
            mapper.registerModule(new GuavaModule());
            mapper.registerModule(new GuavaExtrasModule());
            mapper.registerModule(new AfterburnerModule());
            mapper.registerModule(new FuzzyEnumModule());
            mapper.registerModule(new Jdk8Module());
            mapper.registerModule(new SimpleModule());
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
            javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
            mapper.registerModule(javaTimeModule);


            SimpleModule xssModule = new SimpleModule();
            xssModule.addDeserializer(String.class, new EscapeHtmlDeserializer());
            mapper.registerModule(xssModule);

            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            mapper.setSubtypeResolver(new DiscoverableSubtypeResolver());
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            //序列化BigDecimal时不使用科学计数法输出
            mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
            mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

            return mapper;
        }

        @Override
        public ObjectMapper objectMapper() {
            return this.instance;
        }
    };

    public static ObjectMapper of(){
        return INSTANCE.objectMapper();
    }

    public static <T> T json2Object(final String jsonString, final Class<T> type) {
        try {
            return Strings.isNullOrEmpty(jsonString) ? null : INSTANCE.objectMapper().readValue(jsonString, type);
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T json2Objectλ(final String json, final Class<T> type) {
        return json2Object(unescape(json),type);
    }
    public static boolean isJSON(String json){
        try {
            INSTANCE.objectMapper().readTree(json);
            return true;
        }catch (Exception ex){
            return false;
        }
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(final String json) {
        try {
            return Strings.isNullOrEmpty(json) ? Collections.emptyMap() :INSTANCE.objectMapper().readValue(json, Map.class);
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }

    public static <K,V> Map<K,V> json2Map(final String json,Class<K> kClass,Class<V> vClass) {
        return json2Map(json,Map.class,kClass,vClass);
    }

    public static <K,V> Map<K,V> json2Map(final String json,Class<? extends Map> mapClass,Class<K> kClass,Class<V> vClass) {
        try {
            return Strings.isNullOrEmpty(json) ? Collections.emptyMap() : INSTANCE.objectMapper().readValue(json, buildMapType(mapClass,kClass,vClass));
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> object2Map(final Object data) {
        try {
            return INSTANCE.objectMapper().convertValue(data, Map.class);
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }



    public static <T> T json2Object(final String json, final JavaType type) {
        try {
            return Strings.isNullOrEmpty(json) ? null : INSTANCE.objectMapper().readValue(json, type);
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T json2Object(final String json,final TypeReference<T> valueTypeRef){
        try {
            return Strings.isNullOrEmpty(json) ? null : INSTANCE.objectMapper().readValue(json,valueTypeRef);
        } catch (JsonProcessingException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T json2Object(final byte[] data,final Class<T> type){
        try {
            return data == null ? null : INSTANCE.objectMapper().readValue(data, type);
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T json2Object(final byte[] data,final JavaType type){
        try {
            return data == null ? null : INSTANCE.objectMapper().readValue(data, type);
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T json2Object(final byte[] data, final TypeReference<T> valueTypeRef) {
        try {
            return data == null ? null :INSTANCE.objectMapper().readValue(data, valueTypeRef);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> List<T> json2List(final byte[] data, Class<T> clazz) {
        try {
            return data == null ? Collections.emptyList() : INSTANCE.objectMapper().readValue(data, buildCollectionType(List.class, clazz));
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> List<T> json2LinkedList(final byte[] data, Class<T> clazz) {
        try {
            return data == null ? Collections.emptyList() : INSTANCE.objectMapper().readValue(data, buildCollectionType(LinkedList.class, clazz));
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> List<T> json2List(final String json, Class<T> clazz) {
        try {
            return Strings.isNullOrEmpty(json) ? Collections.emptyList() : INSTANCE.objectMapper().readValue(json, buildCollectionType(List.class, clazz));
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> List<T> json2LinkedList(final String json, Class<T> clazz){
        try {
            return Strings.isNullOrEmpty(json) ? Collections.emptyList() : INSTANCE.objectMapper().readValue(json, buildCollectionType(LinkedList.class, clazz));
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static String object2Json(Object object) {
        try {
            return INSTANCE.objectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new JacksonException(e);
        }
    }

    public static byte[] object2JsonBytes(Object object) {
        try {
            return INSTANCE.objectMapper().writeValueAsBytes(object);
        } catch (Exception e) {
            throw new JacksonException(e);
        }
    }

    public static Optional<String> object2Json2(final Object data) {
        try {
            return data == null ? Optional.empty() : Optional.of(INSTANCE.objectMapper().writeValueAsString(data));
        } catch (final Exception ex) {
            throw new JacksonException(ex);
        }
    }

    /**
     * 构造Collection类型.
     */
    @SuppressWarnings("rawtypes")
    public static JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return INSTANCE.objectMapper().getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 构造Map类型.
     */
    @SuppressWarnings("rawtypes")
    public static JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return INSTANCE.objectMapper().getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public static JavaType buildType(Class<?> parametrized, Class<?>... parameterClasses) {
        return INSTANCE.objectMapper().getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 輸出JSONP格式數據.
     */
    public static String toJsonP(String functionName, Object object) {
        return object2Json(new JSONPObject(functionName, object));
    }

    public static String unescape(String json){
        return StringEscapeUtils.unescapeHtml4(json);
    }

}
