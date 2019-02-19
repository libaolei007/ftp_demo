package com.lbl.ftp;

import java.io.File;

/**
 * 
 * @description: 测试ftp
 * @author: libl
 * @date: 2019年2月18日
 */
public class TestFtp {

	public static void main(String[] args) {
		FtpServerUtil.startFtp("10.0.44.174", 2121, "test", "123456", "D:\\ftp测试");
		FtpCilentUtil ftpClient = new FtpCilentUtil("10.0.44.174", 2121, "test", "123456");
		ftpClient.upload(new File("D:\\ftp测试.txt"));
	}
}
