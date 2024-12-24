package io.github.buzzxu.spuddy.jackson.conv;

/**
 * @author xux
 * @date 2023年04月02日 16:47:13
 */
public interface JsonConvert<T> {
    /**
     *
     * 转换
     * @param target 对象
     * @return 返回结果
     */
    default T convert(T target){
        return target;
    }
}
