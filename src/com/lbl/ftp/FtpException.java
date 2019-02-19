package com.lbl.ftp;
/**
 * 
 * @description: Ftp异常
 * @author: libl  
 * @date: 2019年2月18日
 */
public class FtpException extends RuntimeException {
	private static final long serialVersionUID = -8490149159895201756L;

	public FtpException(Throwable e) {
		super(null == e ? null : e.getClass().getSimpleName() + ":" + e.getMessage(), e);
	}

	public FtpException(String message) {
		super(message);
	}

	public FtpException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public FtpException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public FtpException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
