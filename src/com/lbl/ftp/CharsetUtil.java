package com.lbl.ftp;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 
 * @description: 字符集工具类
 * @author: libl  
 * @date: 2019年2月18日
 */
class CharsetUtil {
	
	/** ISO-8859-1 */
	public static final String ISO_8859_1 = "ISO-8859-1";
	/** UTF-8 */
	public static final String UTF_8 = "UTF-8";
	/** GBK */
	public static final String GBK = "GBK";
	
	/** ISO-8859-1 */
	public static final Charset CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
	/** UTF-8 */
	public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
	/** GBK */
	public static final Charset CHARSET_GBK = Charset.forName(GBK);
	
	private CharsetUtil() {
		super();
	}

	/**
	 * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
	 * 
	 * @see CharsetUtil#defaultCharsetName()
	 * @return 系统字符集编码
	 * @since 3.1.2
	 */
	public static String systemCharsetName() {
		return systemCharset().name();
	}
	
	/**
	 * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
	 * 
	 * @see CharsetUtil#defaultCharsetName()
	 * @return 系统字符集编码
	 * @since 3.1.2
	 */
	public static Charset systemCharset() {
		return FileUtil.isWindows() ? CHARSET_GBK : defaultCharset();
	}
	
	/**
	 * 系统默认字符集编码
	 * 
	 * @return 系统字符集编码
	 */
	public static Charset defaultCharset() {
		return Charset.defaultCharset();
	}
	
}
