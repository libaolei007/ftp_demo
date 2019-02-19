package com.lbl.ftp;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @description: 文件工具类
 * @author: libl  
 * @date: 2019年2月18日
 */
class FileUtil {
	/** Windows路径分隔符 */
	private static final char WINDOWS_SEPARATOR = StrUtil.C_BACKSLASH;
	
	private FileUtil() {
		super();
	}
	
	/**
	 * 返回文件名
	 * 
	 * @param filePath 文件
	 * @return 文件名
	 * @since 4.1.13
	 */
	public static String getName(String filePath) {
		if (null == filePath) {
			return filePath;
		}
		int len = filePath.length();
		if (0 == len) {
			return filePath;
		}
		if (isFileSeparator(filePath.charAt(len - 1))) {
			// 以分隔符结尾的去掉结尾分隔符
			len--;
		}

		int begin = 0;
		char c;
		for (int i = len - 1; i > -1; i--) {
			c = filePath.charAt(i);
			if (isFileSeparator(c)) {
				// 查找最后一个路径分隔符（/或者\）
				begin = i + 1;
				break;
			}
		}

		return filePath.substring(begin, len);
	}
	/**
	 * 获得一个输出流对象
	 * 
	 * @param file 文件
	 * @return 输出流对象
	 * @throws IORuntimeException IO异常
	 */
	public static BufferedOutputStream getOutputStream(File file) throws IORuntimeException {
		try {
			return new BufferedOutputStream(new FileOutputStream(touch(file)));
		} catch (Exception e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 是否为Windows或者Linux（Unix）文件分隔符<br>
	 * Windows平台下分隔符为\，Linux（Unix）为/
	 * 
	 * @param c 字符
	 * @return 是否为Windows或者Linux（Unix）文件分隔符
	 * @since 4.1.11
	 */
	public static boolean isFileSeparator(char c) {
		return StrUtil.SLASH == c || StrUtil.C_BACKSLASH == c;
	}

	public static boolean isWindows() {
		return WINDOWS_SEPARATOR == File.separatorChar;
	}
	
	/**
	 * 创建所给文件或目录的父目录
	 * 
	 * @param file 文件或目录
	 * @return 父目录
	 */
	public static File mkParentDirs(File file) {
		final File parentFile = file.getParentFile();
		if (null != parentFile && false == parentFile.exists()) {
			parentFile.mkdirs();
		}
		return parentFile;
	}

	/**
	 * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
	 * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
	 * 
	 * @param file 文件对象
	 * @return 文件，若路径为null，返回null
	 * @throws IORuntimeException IO异常
	 */
	public static File touch(File file) throws IORuntimeException {
		if (null == file) {
			return null;
		}
		if (!file.exists()) {
			mkParentDirs(file);
			try {
				file.createNewFile();
			} catch (Exception e) {
				throw new IORuntimeException(e);
			}
		}
		return file;
	}
}
