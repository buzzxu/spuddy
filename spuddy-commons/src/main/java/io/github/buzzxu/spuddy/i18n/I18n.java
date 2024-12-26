package io.github.buzzxu.spuddy.i18n;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Strings;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toMap;

/**
 * @author xux
 * @date 2022年08月24日 9:42
 */
public final class I18n {

    private static final ThreadLocal<Locale> localeHolder = ThreadLocal.withInitial(() -> Locale.SIMPLIFIED_CHINESE);
    private static final ConcurrentHashMap<String, Resource> resMap = new ConcurrentHashMap<>();

    static String defaultLocale = Locale.getDefault().toLanguageTag();

    private static Cache<String,String> CACHE = Caffeine.newBuilder()
            .maximumSize(100000) // 最大缓存大小
            .expireAfterWrite(365, TimeUnit.DAYS) // 写入后缓存过期时间
            .build();

    public static Locale get(){
        return localeHolder.get();
    }

    public static void set(Locale locale){
        localeHolder.set(locale);
    }
    public static void set(HttpServletRequest httpRequest){
        Locale locale = of(httpRequest);
        localeHolder.set(locale);
        LocaleContextHolder.setLocale(locale,true);
    }

    public static void clear(){
        localeHolder.remove();
        LocaleContextHolder.resetLocaleContext();
    }
    public static Locale of(HttpServletRequest httpRequest) {
        String lang = find(httpRequest);
        if(Strings.isNullOrEmpty(lang) || StringUtils.equals(lang,"null")){
            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.forLanguageTag(lang);
    }
    public static String find(HttpServletRequest httpRequest) {
        String lang = httpRequest.getHeader("Accept-Language");
        // 先从Header里面获取
        if(Strings.isNullOrEmpty(lang) || StringUtils.equals(lang,"null")){
            // 获取不到再从Parameter中拿
            lang = httpRequest.getParameter("lang");
            // 还是获取不到再从Cookie中拿
            if(Strings.isNullOrEmpty(lang) || StringUtils.equals(lang,"null")){
                Cookie[] cookies = httpRequest.getCookies();
                if(cookies != null){
                    for (Cookie cookie : cookies) {
                        if("lang".equals(cookie.getName())){
                            lang = cookie.getValue();
                            break;
                        }
                    }
                }
            }
        }
        return lang;
    }

    public static Locale toLocale(String locale) {
        if(Strings.isNullOrEmpty(locale)){
            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.forLanguageTag(locale);
    }

    public static Resource use(String baseName){
        return use(baseName, get());
    }



    private static Resource use(String baseName, String locale) {
        String resKey = baseName + locale;
        Resource res = resMap.get(resKey);
        if (res == null) {
            res = new Resource(baseName, locale);
            resMap.put(resKey, res);
        }
        return res;
    }
    public static Resource use(String baseName, Locale locale) {
        String resKey = baseName + (locale == null ? defaultLocale : locale.toLanguageTag());
        Resource res = resMap.get(resKey);
        if (res == null) {
            res = new Resource(baseName,  (locale == null ? defaultLocale : locale.toLanguageTag()));
            resMap.put(resKey, res);
        }
        return res;
    }


    public static String $(String key){
        return get(get(),key);
    }
    public static String $(String key, Function<String,String> function){
        return get(get(),key,function);
    }
    public static String $(String key,Object... arguments){
        return $(get(),key,arguments);
    }
    public static String $(String key, Function<String,String> function,Object... arguments){
        return $(get(),key,function,arguments);
    }
    public static String $(Locale locale,String key){
        return get(locale,key);
    }
    public static String $(Locale locale,String key, Function<String,String> function){
        return get(locale,key,function);
    }
    public static String $(Locale locale,String key,Object... arguments){
        String value = get(locale,key);
        if(!Strings.isNullOrEmpty(value)){
            return new MessageFormat(value,locale).format(arguments);
        }
        return MessageFormat.format(key,arguments);
    }
    public static String $(Locale locale,String key,Function<String,String> function,Object... arguments){
        String value = get(locale,key,function);
        if(!Strings.isNullOrEmpty(value)){
            return new MessageFormat(value,locale).format(arguments);
        }
        return MessageFormat.format(key,arguments);
    }

    public static void load(Locale locale, Map<String,String> hash){
        checkArgument(locale != null,"请设置Locale");
        checkArgument(hash != null && !hash.isEmpty(),"请设置多语言参照表");
        Map<String,String> map = hash.entrySet().stream().collect(toMap(entry-> locale.toLanguageTag()+":"+entry.getKey(),Map.Entry::getValue));
        CACHE.putAll(map);
    }
    private static String get(Locale locale,String key){
        String val = CACHE.getIfPresent(locale.toLanguageTag()+":"+key);
        return Strings.isNullOrEmpty(val) ? key : val;
    }
    private static String get(Locale locale, String key, Function<String,String> function){
        String val = CACHE.get(locale.toLanguageTag()+":"+key,function);
        return Strings.isNullOrEmpty(val) ? key : val;
    }
    public static void put(Locale locale,String key,String value){
        CACHE.put(locale.toLanguageTag() +":"+key.trim(),value.trim());
    }

    public static void put(String language,String region,String key,String value){
        CACHE.put(language.strip()+"-"+region +":"+key.trim(),value.trim());
    }

}
