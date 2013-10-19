package net.loveyu.remotecontrol;

/**
 * ������Ϣ������
 * 
 * @author loveyu
 * 
 */
public enum SendMsgType {
	/**
	 * ������Ϣ
	 */
	COMMAND, // 01
	/**
	 * ��ʾ��Ϣ
	 */
	NOTICE, // 02
	/**
	 * �ļ���Ϣ
	 */
	FILE, // 03
	/**
	 * ������Ϣ
	 */
	WARNING, // 04
	/**
	 * �ն���Ϣ
	 */
	TERMINAL, // 05
	/**
	 * ������Ϣ
	 */
	FUNCTION, // 06
	/**
	 * ��ִ��Ϣ
	 */
	CALLBACK, // 07
	/**
	 * ��¼��Ϣ
	 */
	LOGIN, // 08
	/**
	 * �ǳ���Ϣ
	 */
	LOGOUT, // 09
	/**
	 * ��ͼ��Ϣ
	 */
	SCREENSHOT, // 10
	/**
	 * ���������Ϣ
	 */
	TASK, // 11
}
