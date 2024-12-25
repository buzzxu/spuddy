package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * @author xux
 * @date 2024年04月19日 21:04:32
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class MenuRouter {
    private String name;
    private String path;
    private String component;
    private Meta meta;
    private List<MenuRouter> children;


    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Meta{
        private String title;
        @JsonProperty("i18nKey")
        private String i18n;
        private String icon;
        private Integer order;
    }
}
