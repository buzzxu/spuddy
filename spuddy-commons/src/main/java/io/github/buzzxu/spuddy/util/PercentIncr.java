package io.github.buzzxu.spuddy.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author xux
 * @date 2022年09月04日 22:34
 */
public class PercentIncr {

    public static String λ(BigDecimal amount,BigDecimal pre){
        BigDecimal ratio = amount.subtract(pre).divide(pre,4, RoundingMode.HALF_DOWN).multiply(new BigDecimal(100)).setScale(2,RoundingMode.HALF_DOWN);
        return ratio.stripTrailingZeros().toPlainString();
    }
}
