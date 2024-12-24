package io.github.buzzxu.spuddy.util;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.*;

public abstract class Random {

    public static java.util.Random threadLocalRandom() {
        return ThreadLocalRandom.current();
    }

    public static SecureRandom secureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {// NOSONAR
            return new SecureRandom();
        }
    }
    /**
     * 创建一个长度为count的数字字符随机字符串
     * @param count
     * @return
     */
    public static String numeric(int count){
        return randomNumeric(count);
    }

    /**
     * 创建一个长度为count的字母字符随机字符串
     * @param count
     * @return
     */
    public static String alphabetic(int count){
        return randomAlphabetic(count);
    }

    /**
     * 创建一个长度为count的包含字符字符和数字字符的随机字符串
     * @param count
     * @return
     */
    public static String alphanumeric(int count){
        return randomAlphanumeric(count);
    }
}
