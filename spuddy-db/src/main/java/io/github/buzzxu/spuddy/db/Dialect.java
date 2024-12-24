package io.github.buzzxu.spuddy.db;

public interface Dialect {

    default String ifnull(String column,String value){
        return "IFNULL(%s,%s)".formatted(column,value);
    }

    default String page(String sql,int number,int size){
        String beginrow = String.valueOf((number - 1) * size);
        return sql + " LIMIT " + size + " OFFSET " + beginrow;
    }

    default String validationQuery(){
        return "SELECT 1";
    }

}
