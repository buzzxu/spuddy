package io.github.buzzxu.spuddy;



import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import org.apache.commons.lang3.StringUtils;

public enum Stage {
    DEFAULT(0,"default"),
    DEVEL(1,"devel"),
    TEST(2,"test"),
    PRODUCT(3,"product"),
    DOCKER(4,"docker"),
    UNIT_TEST(5,"unit-test"),
    ;

    private int level;
    private String stage;

    Stage(int level, String stage) {
        this.level = level;
        this.stage = stage;
    }

    public static Stage N(String name){
        for(Stage val : values()){
            if(StringUtils.endsWithIgnoreCase(val.stage,name)){
                return val;
            }
        }
        throw ApplicationException.raise("e.x.,stage=devel|default|test|product|docker|unit-test");
    }

    public boolean isDevTest(){
        return level <= TEST.level;
    }
    public int level() {
        return level;
    }

    public String stage() {
        return stage;
    }
}
