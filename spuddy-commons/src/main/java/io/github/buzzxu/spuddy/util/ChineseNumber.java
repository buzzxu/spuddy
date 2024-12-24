package io.github.buzzxu.spuddy.util;

/**
 * 将阿拉伯数字转换成中文大写或小写数字，具体用法可参考测试代码
 * 
 * @author xiang.xu
 * 
 */
public class ChineseNumber {
	private static final int RADIX = 10;

	private static final String[] LOWER_UNIT = { "", "十", "百", "千", "万", "十",
			"百", "千", "亿", "十", "百", "千", "万", "十", "百", "千", "万" };

	private final String[] LOWER = { "零", "一", "二", "三", "四", "五", "六", "七",
			"八", "九" };

	private static final String[] UPPER_UNIT = { "", "拾", "佰", "仟", "万", "拾",
			"佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟", "万" };

	private final String[] UPPER = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒",
			"捌", "玖" };

	private final int number;

	private final boolean abbreviation;

	/**
	 * 
	 * @param number
	 * @param isShort
	 *            是否缩写
	 */
	public ChineseNumber(int number, boolean isShort) {
		this.number = number;
		this.abbreviation = isShort;
	}

	/**
	 * 中文数字不进行缩写。如18->一十八
	 * 
	 * @param number
	 */
	public ChineseNumber(int number) {
		this(number, false);
	}

	public String lower() {
		return format(LOWER, LOWER_UNIT);
	}

	public String upper() {
		return format(UPPER, UPPER_UNIT);
	}

	private String format(final String[] chinese, final String[] unit) {
		if (number == 0) {
			return chinese[number];
		}

		if (abbreviation == true && canAbbreviate(number)) {
			return formatShort(chinese, unit);
		}
		String result = "";

		int leftNumber = number;
		int rightNumber = 0;
		int currentNumber = 0;
		int bit = 0;
		while (leftNumber > 0) {
			rightNumber = currentNumber;
			currentNumber = leftNumber % RADIX;
			leftNumber = leftNumber / RADIX;

			if (currentNumber > 0) {
				result = chinese[currentNumber] + unit[bit] + result;
			} else if (rightNumber > 0) {
				result = chinese[currentNumber] + result;
			}

			if (bit % 4 == 0 && currentNumber == 0) {
				result = unit[bit] + result;
			}

			bit++;
		}

		return result;

	}

	/**
	 * 缩写 18 为 十八 或 拾捌
	 */
	private String formatShort(String[] chinese, String[] unit) {
		// bugfix 修复输入"10"输出"十零"的Bug
		if (number == 10) {
			return unit[1];
		}
		return unit[1] + chinese[number % RADIX];
	}

	/**
	 * 能否缩写
	 */
	private boolean canAbbreviate(int number2) {
        return number2 > 9 && number2 <= 19;
    }
	// public static void main(String[] args){
	// for(int i=0;i<30;i++){
	// System.out.println(new ChineseNumber(i,true).lower());
	// System.out.println(new ChineseNumber(i).lower());
	// System.out.println("=============");
	// }
	// }
}
