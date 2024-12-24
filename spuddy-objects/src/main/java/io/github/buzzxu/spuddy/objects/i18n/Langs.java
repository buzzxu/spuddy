package io.github.buzzxu.spuddy.objects.i18n;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author xux
 * @date 2023年07月02日 22:39:34
 */
public class Langs extends HashMap<String,Lang> {
    public Lang get(Locale locale){
        return get(locale.toLanguageTag());
    }

    public Langs put(Locale locale,Lang lang){
        return putOf(locale.toLanguageTag(),lang);
    }
    public Langs putOf(String language,Lang lang){
        put(language,lang);
        return this;
    }
    public static Langs of(Locale locale,Lang lang){
        return new Langs().put(locale,lang);
    }
    public static Langs of(String language,Lang lang){
        return new Langs().putOf(language,lang);
    }
}
