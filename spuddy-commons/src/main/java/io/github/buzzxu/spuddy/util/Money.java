package io.github.buzzxu.spuddy.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2019-10-08 20:25
 **/
public final class Money extends CurrencyConvert {

    public static final BigDecimal percent = new BigDecimal("0.01");

    /**
     * 金额的百分比计算
     * @param amount    总金额
     * @param rate      费率  1=1% 20=20%
     * @return
     */
    public static BigDecimal percentage(BigDecimal amount,Float rate){
        if(rate == null){
            //如果未设置费率，则返回原金额
            return BigDecimal.ZERO;
        }
        return amount.multiply(percent.multiply(new BigDecimal(rate))).setScale(2, RoundingMode.HALF_UP);
    }
}
