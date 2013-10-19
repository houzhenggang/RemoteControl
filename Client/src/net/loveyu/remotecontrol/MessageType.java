package net.loveyu.remotecontrol;

/**
 * ��Ϣ����
 * 
 * @author loveyu
 * 
 */
public enum MessageType {
	/**
	 * ��ִ��Ϣ
	 */
	Callback, // 01

	/**
	 * �����ı���ʾ
	 */
	Text, // 02

	/**
	 * ��¼״̬��Ϣ
	 */
	Login, // 03

	/**
	 * ִ�д�����Ϣ
	 */
	RunError, // 04

	/**
	 * �ļ���Ϣ
	 */
	File, // 05

	/**
	 * ��ͼ��Ϣ
	 */
	Picture, // 06

	/**
	 * �ն���Ϣ
	 */
	Terminal, // 07

	/**
	 * �ն˴�����Ϣ
	 */
	TerminalError, // 08

	/**
	 * ������Ϣ
	 */
	Task, // 09

	/**
	 * δ֪��Ϣ
	 */
	Unknow
}
