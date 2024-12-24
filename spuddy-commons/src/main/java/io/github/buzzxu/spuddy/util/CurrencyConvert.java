package io.github.buzzxu.spuddy.util;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

public  class CurrencyConvert {

    private static final ThreadLocal<DecimalFormat> DEFAULT_FORMAT = createThreadLocalNumberformat("0.00");
    private static final ThreadLocal<DecimalFormat> PRETTY_FORMAT = createThreadLocalNumberformat("#,##0.00");
    // 配置文件中中文单位的分隔符
    @SuppressWarnings("unused")
    private static final String SPLIT = ",";

    // 最大转换数值
    private static final int MAX_NUMBER = 10000000;
    /**
     * 中文金额单位数组 String[]{"分", "角", "圆", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿",
     * "拾", "佰", "仟"}
     */
	private static final String[] chineseUnit = new String[] { "分", "角", "圆",
			"拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟"};

    /**
     * 中文数字字符数组 String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"}
     */
    private static final String[] chineseNumber = new String[]{"零", "壹", "贰",
            "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    /**
     * 中文描述 String[]{"整", "负"}
     */
    private static final String[] chineseDesc = new String[]{"整", "负"};

    private static ThreadLocal<DecimalFormat> createThreadLocalNumberformat(final String pattern) {
        return ThreadLocal.withInitial(() -> {
            DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
            df.applyPattern(pattern);
            return df;
        });
    }

    public static BigDecimal fen2yuan(BigDecimal num) {
        return num != null ? num.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public static BigDecimal fen2yuan(long num) {
        return fen2yuan(new BigDecimal(num));
    }
    public static BigDecimal fen2yuan(Long num) {
        return num == null ? BigDecimal.ZERO : fen2yuan(new BigDecimal(num));
    }
    public static BigDecimal fen2Yuan(long num) {
        return fen2yuan(num);
    }
    public static BigDecimal fen2Yuan(Long num) {
        return num == null ? BigDecimal.ZERO : fen2yuan(num);
    }
    public static BigDecimal fen2yuan(String num) {
        return Strings.isNullOrEmpty(num)? BigDecimal.ZERO : fen2yuan(new BigDecimal(num));
    }
    public static String fen2Yuan(Integer fen) {
        return fen != null ? BigDecimal.valueOf(Double.valueOf(fen) / 100).setScale(2, RoundingMode.HALF_UP).toPlainString() : "0";
    }

    public static BigDecimal yuan2fen(String y) {
        return !Strings.isNullOrEmpty(y) ? new BigDecimal(Math.round((new BigDecimal(y)).multiply(new BigDecimal(100)).doubleValue())) : BigDecimal.ZERO;
    }
    public static BigDecimal yuan2fen(double y) {
        return yuan2fen(String.valueOf(y));
    }

    public static BigDecimal yuan2fen(BigDecimal y) {
        return y != null ? yuan2fen(y.toString()) : BigDecimal.ZERO;
    }

    public static Long yuan2Fen(String yuan) {
        return !Strings.isNullOrEmpty(yuan) ? new BigDecimal(yuan).setScale(2, RoundingMode.HALF_UP ).multiply(new BigDecimal(100)).longValue(): 0L;
    }
    public static Integer yuan2Fenλ(String yuan) {
        return !Strings.isNullOrEmpty(yuan) ? new BigDecimal(yuan).setScale(2, RoundingMode.HALF_UP ).multiply(new BigDecimal(100)).intValue() : 0;
    }
    public static Long yuan2Fen(BigDecimal yuan) {
        return yuan2fen(yuan).longValue();
    }
    public static Integer yuan2Fenλ(BigDecimal yuan) {
        return yuan2fen(yuan).intValue();
    }
    public static String format(BigDecimal number) {
        return format(number.doubleValue());
    }

    public static String format(double number) {
        return DEFAULT_FORMAT.get().format(number);
    }

    public static String prettyFormat(BigDecimal number) {
        return prettyFormat(number.doubleValue());
    }

    public static String prettyFormat(double number) {
        return PRETTY_FORMAT.get().format(number);
    }

    public static String format(BigDecimal number, String pattern) {
        return format(number.doubleValue(), pattern);
    }

    public static String format(double number, String pattern) {
        DecimalFormat df = null;
        if (StringUtils.isEmpty(pattern)) {
            df = PRETTY_FORMAT.get();
        } else {
            df = (DecimalFormat) DecimalFormat.getInstance();
            df.applyPattern(pattern);
        }

        return df.format(number);
    }

    public static BigDecimal parseString(String numberStr) throws ParseException {
        return new BigDecimal(DEFAULT_FORMAT.get().parse(numberStr).doubleValue());
    }

    public static BigDecimal parsePrettyString(String numberStr) throws ParseException {
        return new BigDecimal(PRETTY_FORMAT.get().parse(numberStr).doubleValue());
    }

    public static BigDecimal parseString(String numberStr, String pattern) throws ParseException {
        DecimalFormat df = null;
        if (StringUtils.isEmpty(pattern)) {
            df = PRETTY_FORMAT.get();
        } else {
            df = (DecimalFormat) DecimalFormat.getInstance();
            df.applyPattern(pattern);
        }

        return new BigDecimal(df.parse(numberStr).doubleValue());
    }

    /**
     * 将数字金额转换为中文金额
     *
     * @param bigdMoneyNumber
     * @return
     */
    public static String toChinese(BigDecimal bigdMoneyNumber) {
        String strChineseCurrency = "";
        if (bigdMoneyNumber.intValue() < MAX_NUMBER) {
            // 零数位标记
            boolean bZero = true;
            // 中文金额单位下标
            int ChineseUnitIndex = 0;

            if (bigdMoneyNumber.intValue() == 0) {
                strChineseCurrency = chineseNumber[0] + chineseUnit[2]
                        + chineseDesc[0];
				return strChineseCurrency;
			}
			// 处理小数部分，四舍五入
			double doubMoneyNumber = Math
					.round(bigdMoneyNumber.doubleValue() * 100);
			// 是否负数
			boolean bNegative = doubMoneyNumber < 0;
			// 取绝对值
			doubMoneyNumber = Math.abs(doubMoneyNumber);

			// 循环处理转换操作
			while (doubMoneyNumber > 0) {
				// 整的处理(无小数位)
				if (ChineseUnitIndex == 2 && strChineseCurrency.length() == 0) {
					strChineseCurrency = strChineseCurrency + chineseDesc[0];
				}

				// 非零数位的处理
				if (doubMoneyNumber % 10 > 0) {
					strChineseCurrency = chineseNumber[(int) doubMoneyNumber % 10]
							+ chineseUnit[ChineseUnitIndex]
							+ strChineseCurrency;
					bZero = false;
				}
				// 零数位的处理
				else {
					// 元的处理(个位)
					if (ChineseUnitIndex == 2) {
						// 段中有数字
						if (doubMoneyNumber > 0) {
							strChineseCurrency = chineseUnit[ChineseUnitIndex]
									+ strChineseCurrency;
							bZero = true;
						}
					}
					// 万、亿数位的处理
					else if (ChineseUnitIndex == 6 || ChineseUnitIndex == 10) {
						// 段中有数字
						if (doubMoneyNumber % 1000 > 0) {
							strChineseCurrency = chineseUnit[ChineseUnitIndex]
									+ strChineseCurrency;
						}

					}
					// 前一数位非零的处理
					if (!bZero && ChineseUnitIndex != 6
							&& ChineseUnitIndex != 10) {
						strChineseCurrency = chineseNumber[0]
								+ strChineseCurrency;
					}
					bZero = true;
				}
				doubMoneyNumber = Math.floor(doubMoneyNumber / 10);
				ChineseUnitIndex++;
			}

			// 负数的处理
			if (bNegative) {
				strChineseCurrency = chineseDesc[1] + strChineseCurrency;
			}
		}
		return strChineseCurrency;
	}

}