package io.github.buzzxu.spuddy.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-09 13:14
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor @SuperBuilder(toBuilder = true)
public abstract class Id<ID> implements Argument, Serializable {

    protected ID id;
    protected String creater;
    private Long createrId;
    protected String updater;
    protected Long updaterId;
    @JsonIgnore
    protected boolean deleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updatedAt;


    public Id(ID id, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public <M extends Model> void from(M model){
        //nothing to do
    }
}
