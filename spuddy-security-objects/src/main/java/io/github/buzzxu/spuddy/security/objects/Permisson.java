package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class Permisson implements Serializable {
    @Serial
    private static final long serialVersionUID = 3818797694716243425L;

    private int id;
    /**
     * 权限类型 1=权限标识,2=URL,3=页面元素ID
     */
    private int type;
    private String name;
    private String code;
    private String target;
    private String description;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Permisson(int id, int type, String name, String code, String target, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.code = code;
        this.target = target;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
