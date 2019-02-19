package com.lbl.ftp;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
/**
 * 
 * @description: 字符串工具类
 * @author: libl  
 * @date: 2019年2月18日
 */
class StrUtil {
	public static final String EMPTY = "";
	public static final String EMPTY_JSON = "{}";
	public static final char C_BACKSLASH = '\\';
	public static final char DELIM_END = '}';
	public static final char C_DELIM_START = '{';
	public static final char C_CR = '\r';
	public static final char C_LF = '\n';
	public static final char C_TAB = '	';
	public static final String SPACE = " ";
	public static final char SLASH = '/';
	
	private StrUtil() {
		super();
	}

	/**
	 * 格式化文本, {} 表示占位符<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 * 
	 * @param template 文本模板，被替换的部分用 {} 表示
	 * @param params 参数值
	 * @return 格式化后的文本
	 */
	public static String format(CharSequence template, Object... params) {
		if (null == template) {
			return null;
		}
		if ((params == null || params.length == 0) || isBlank(template)) {
			return template.toString();
		}
		return format(template.toString(), params);
	}
	
	/**
	 * 格式化字符串<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 		通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 		转义{}： 	format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 		转义\：		format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 * @param strPattern 字符串模板
	 * @param argArray 参数列表
	 * @return 结果
	 */
	public static String format(final String strPattern, final Object... argArray) {
		if (StrUtil.isBlank(strPattern) || (argArray == null || argArray.length == 0)) {
			return strPattern;
		}
		final int strPatternLength = strPattern.length();

		//初始化定义好的长度以获得更好的性能
		StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

		int handledPosition = 0;//记录已经处理到的位置
		int delimIndex;//占位符所在位置
		for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
			delimIndex = strPattern.indexOf(StrUtil.EMPTY_JSON, handledPosition);
			if (delimIndex == -1) {//剩余部分无占位符
				if (handledPosition == 0) { //不带占位符的模板直接返回
					return strPattern;
				} else { //字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
					sbuf.append(strPattern, handledPosition, strPatternLength);
					return sbuf.toString();
				}
			} else {
				if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == StrUtil.C_BACKSLASH) {//转义符
					if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == StrUtil.C_BACKSLASH) {//双转义符
						//转义符之前还有一个转义符，占位符依旧有效
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
						handledPosition = delimIndex + 2;
					} else {
						//占位符被转义
						argIndex--;
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(StrUtil.C_DELIM_START);
						handledPosition = delimIndex + 1;
					}
				} else {//正常占位符
					sbuf.append(strPattern, handledPosition, delimIndex);
					sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
					handledPosition = delimIndex + 2;
				}
			}
		}
		// append the characters following the last {} pair.
		//加入最后一个占位符后所有的字符
		sbuf.append(strPattern, handledPosition, strPattern.length());
		
		return sbuf.toString();
	}
	
	/**
	 * 对象是否为数组对象
	 * 
	 * @param obj 对象
	 * @return 是否为数组对象，如果为{@code null} 返回false
	 */
	public static boolean isArray(Object obj) {
		if (null == obj) {
			return false;
		}
		return obj.getClass().isArray();
	}
	
	/**
	 * 字符串是否为空白 空白的定义如下： <br>
	 * 1、为null <br>
	 * 2、为不可见字符（如空格）<br>
	 * 3、""<br>
	 * 
	 * @param str 被检测的字符串
	 * @return 是否为空
	 */
	public static boolean isBlank(CharSequence str) {
		int length;

		if ((str == null) || ((length = str.length()) == 0)) {
			return true;
		}

		for (int i = 0; i < length; i++) {
			// 只要有一个非空字符即为非空字符串
			if (!isBlankChar(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 是否空白符<br>
	 * 空白符包括空格、制表符、全角空格和不间断空格<br>
	 * 
	 * @param c 字符
	 * @return 是否空白符
	 * @see Character#isWhitespace(int)
	 * @see Character#isSpaceChar(int)
	 * @since 4.0.10
	 */
	public static boolean isBlankChar(char c) {
		return isBlankChar((int) c);
	}
	/**
	 * 是否空白符<br>
	 * 空白符包括空格、制表符、全角空格和不间断空格<br>
	 * 
	 * @see Character#isWhitespace(int)
	 * @see Character#isSpaceChar(int)
	 * @param c 字符
	 * @return 是否空白符
	 * @since 4.0.10
	 */
	public static boolean isBlankChar(int c) {
		return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a';
	}
	
	/**
	 * 字符串是否为空，空的定义如下:<br>
	 * 1、为null <br>
	 * 2、为""<br>
	 * 
	 * @param str 被检测的字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}
	
	/**
	 * 字符串是否为非空白 空白的定义如下： <br>
	 * 1、不为null <br>
	 * 2、不为""<br>
	 * 
	 * @param str 被检测的字符串
	 * @return 是否为非空
	 */
	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}
	
	/**
	 * 去掉指定后缀
	 * 
	 * @param str 字符串
	 * @param suffix 后缀
	 * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
	 */
	public static String removeSuffix(CharSequence str, CharSequence suffix) {
		if (isEmpty(str) || isEmpty(suffix)) {
			return str(str);
		}

		final String str2 = str.toString();
		if (str2.endsWith(suffix.toString())) {
			return subPre(str2, str2.length() - suffix.length());// 截取前半段
		}
		return str2;
	}
	
	/**
	 * {@link CharSequence} 转为字符串，null安全
	 * 
	 * @param cs {@link CharSequence}
	 * @return 字符串
	 */
	public static String str(CharSequence cs) {
		return null == cs ? null : cs.toString();
	}

	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj 对象
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String str(Object obj, Charset charset) {
		if (null == obj) {
			return null;
		}

		if (obj instanceof String) {
			return (String) obj;
		} else if (obj instanceof byte[]) {
			return str((byte[]) obj, charset);
		} else if (obj instanceof Byte[]) {
			return str((Byte[]) obj, charset);
		} else if (obj instanceof ByteBuffer) {
			return str((ByteBuffer) obj, charset);
		} else if (isArray(obj)) {
			return toString(obj);
		}

		return obj.toString();
	}
	
	/**
	 * 改进JDK subString<br>
	 * index从0开始计算，最后一个字符为-1<br>
	 * 如果from和to位置一样，返回 "" <br>
	 * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
	 * 如果经过修正的index中from大于to，则互换from和to example: <br>
	 * abcdefgh 2 3 =》 c <br>
	 * abcdefgh 2 -3 =》 cde <br>
	 * 
	 * @param str String
	 * @param fromIndex 开始的index（包括）
	 * @param toIndex 结束的index（不包括）
	 * @return 字串
	 */
	public static String sub(CharSequence str, int fromIndex, int toIndex) {
		if (isEmpty(str)) {
			return str(str);
		}
		int len = str.length();

		if (fromIndex < 0) {
			fromIndex = len + fromIndex;
			if (fromIndex < 0) {
				fromIndex = 0;
			}
		} else if (fromIndex > len) {
			fromIndex = len;
		}

		if (toIndex < 0) {
			toIndex = len + toIndex;
			if (toIndex < 0) {
				toIndex = len;
			}
		} else if (toIndex > len) {
			toIndex = len;
		}

		if (toIndex < fromIndex) {
			int tmp = fromIndex;
			fromIndex = toIndex;
			toIndex = tmp;
		}

		if (fromIndex == toIndex) {
			return EMPTY;
		}

		return str.toString().substring(fromIndex, toIndex);
	}
	
	/**
	 * 切割指定位置之前部分的字符串
	 * 
	 * @param string 字符串
	 * @param toIndex 切割到的位置（不包括）
	 * @return 切割后的剩余的前半部分字符串
	 */
	public static String subPre(CharSequence string, int toIndex) {
		return sub(string, 0, toIndex);
	}
	
	/**
	 * 数组或集合转String
	 * 
	 * @param obj 集合或数组对象
	 * @return 数组字符串，与集合转字符串格式相同
	 */
	public static String toString(Object obj) {
		if (null == obj) {
			return null;
		}
		if (isArray(obj)) {
			try {
				return Arrays.deepToString((Object[]) obj);
			} catch (Exception e) {
				final String className = obj.getClass().getComponentType().getName();
				switch (className) {
				case "long":
					return Arrays.toString((long[]) obj);
				case "int":
					return Arrays.toString((int[]) obj);
				case "short":
					return Arrays.toString((short[]) obj);
				case "char":
					return Arrays.toString((char[]) obj);
				case "byte":
					return Arrays.toString((byte[]) obj);
				case "boolean":
					return Arrays.toString((boolean[]) obj);
				case "float":
					return Arrays.toString((float[]) obj);
				case "double":
					return Arrays.toString((double[]) obj);
				default:
					throw new RuntimeException(e);
				}
			}
		}
		return obj.toString();
	}
	
	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj 对象
	 * @return 字符串
	 */
	public static String utf8Str(Object obj) {
		return str(obj, CharsetUtil.CHARSET_UTF_8);
	}
}
