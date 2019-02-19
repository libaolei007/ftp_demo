package com.lbl.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @description: 创建开启FTP服务器
 * @author: libl
 * @date: 2019年2月18日
 */
public class FtpServerUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpServerUtil.class);

	public static FtpServer ftpServer;

	private FtpServerUtil() {
	}

	/**
	 * 存放ftp服务
	 * 
	 * @param fs
	 */
	public static void setFtpServer(FtpServer fs) {
		ftpServer = fs;
	}

	/**
	 * 获取ftp服务
	 * 
	 * @return
	 */
	public static FtpServer getFtpServer() {
		return ftpServer;
	}

	/**
	 * 开启ftp服务
	 * 
	 * @param ftpIp
	 * @param ftpPort
	 * @param ftpUsername
	 * @param ftpPassword
	 * @param ftpDir
	 * @return
	 */
	public static boolean startFtp(String ftpIp, Integer ftpPort, String ftpUsername, String ftpPassword,
			String ftpDir) {
		return createFtpServerAndStart(ftpIp, ftpPort, ftpUsername, ftpPassword, ftpDir);
	}

	/**
	 * 创建并启动ftp服务器
	 * 
	 * @param ftpIp
	 * @param ftpPort
	 * @param ftpUsername
	 * @param ftpPassword
	 * @param homeDirectory
	 * @return
	 */
	private static boolean createFtpServerAndStart(String ftpIp, Integer ftpPort, String ftpUsername,
			String ftpPassword, String homeDirectory) {
		boolean flag = true;
		if (!StringUtils.isEmpty(ftpUsername) && !StringUtils.isEmpty(ftpPassword) && ftpPort != null
				&& !StringUtils.isEmpty(homeDirectory)) {
			File homeFile = new File(homeDirectory);
			if (!homeFile.exists()) {
				homeFile.mkdirs();
			}
			ListenerFactory factory = new ListenerFactory();
			// 设置监听端口
			factory.setPort(ftpPort);
			factory.setServerAddress(ftpIp);
			BaseUser user = new BaseUser();
			user.setName(ftpUsername);
			// 如果不设置密码就是匿名用户
			user.setPassword(ftpPassword);
			user.setHomeDirectory(homeDirectory);
			List<Authority> authorities = new ArrayList<>();
			// 增加写权限
			authorities.add(new WritePermission());
			user.setAuthorities(authorities);
			FtpServer server = getFtpServer();
			if (server != null && !server.isStopped()) {
				server.stop();
				LOGGER.info("FTP服务器成功停止" + server);
			}
			FtpServerFactory ftpServerFactory = new FtpServerFactory();
			// 替换默认监听
			ftpServerFactory.addListener("default", factory.createListener());
			// 增加该用户
			try {
				ftpServerFactory.getUserManager().save(user);
			} catch (FtpException e) {
				LOGGER.error("FTP服务器启动失败{}", e.getMessage(), e);
				return flag;
			}
			FtpServer newServer = ftpServerFactory.createServer();
			try {
				newServer.start();
				// 存放ftp服务实例
				setFtpServer(newServer);
			} catch (FtpException e) {
				LOGGER.error("FTP服务器启动失败{}", e.getMessage(), e);
				return flag;
			}
			LOGGER.info("FTP服务器启动成功,ftp信息是：fptIp:{}, ftpPort:{}, ftpUsername:{}, ftpPassword:{}, ftpDir:{}", ftpIp,
					ftpPort, ftpUsername, ftpPassword, homeDirectory);
		} else {
			LOGGER.error("启动FTP的配置信息缺失，FTP服务器配置信息：fptIp:{}, ftpPort:{}, ftpUsername:{}, ftpPassword:{}, ftpDir:{}",
					ftpIp, ftpPort, ftpUsername, ftpPassword, homeDirectory);
			flag = false;
		}
		return flag;
	}
}
