package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author xux
 * @date 2024年04月23日 16:01:35
 */
public enum RoleType {
    PROTECTED(0,"系统角色"),
    CUSTOM(1,"自定义"),
    UNKNOWN(-1,"未知"),
    ;
    private final int value;
    private final String text;

    RoleType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    @JsonCreator
    public static RoleType of(int value) {
        for (RoleType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN;
    }

    @JsonValue
    public int value() {
        return value;
    }
    public String text() {
        return text;
    }
}
