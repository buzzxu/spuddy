package io.github.buzzxu.spuddy.i18n;

import com.google.common.base.Strings;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author xux
 * @date 2022年08月25日 14:02
 */
public class Resource {

    private final ResourceBundle resourceBundle;

    public Resource(String baseName, String locale) {
        this(baseName,I18n.toLocale(locale));
    }
    public Resource(String baseName, Locale locale) {
        if (Strings.isNullOrEmpty(baseName)) {
            throw new IllegalArgumentException("baseName can not be blank");
        } else if (locale == null) {
            throw new IllegalArgumentException("locale can not be blank, the format like this: zh_CN or en_US");
        } else {
            this.resourceBundle = ResourceBundle.getBundle(baseName, locale);
        }
    }

    public String get(String key) {
        return I18n.$(resourceBundle.getLocale(),key,v-> this.resourceBundle.getString(key));
    }

    public String format(String key, Object... arguments) {
        return I18n.$(key, v-> this.resourceBundle.getString(key),arguments);
    }

    public ResourceBundle of() {
        return this.resourceBundle;
    }
}
