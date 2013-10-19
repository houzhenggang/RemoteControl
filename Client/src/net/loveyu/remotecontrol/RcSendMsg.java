package net.loveyu.remotecontrol;

import java.net.DatagramPacket;
import android.text.format.Time;

/**
 * ������Ϣ������
 * 
 * @author loveyu
 * 
 */
public class RcSendMsg {
	/**
	 * �����͵�������Ϣ
	 */
	public DatagramPacket pack;
	/**
	 * ���͵���Ϣ����
	 */
	public SendMsgType type;
	/**
	 * �����Ϣ����ʱ�ռ�
	 */
	public byte[] buf;
	/**
	 * ��ϢID
	 */
	public int id;
	/**
	 * IDת�����ַ���
	 */
	public String IdString;
	/**
	 * ��������ϢID
	 */
	public static int ID = 0;
	/**
	 * ��Ϣ�ĳ����ط�����
	 */
	public int tryNum = 0;
	/**
	 * ��Ϣ�ϴη���ʱ��
	 */
	public Time time;

	/**
	 * ������Ϣ
	 */
	private RcSendMsg() {
		time = new Time();
		time.setToNow();
	}

	/**
	 * ������¼��Ϣ
	 * 
	 * @return
	 */
	public static RcSendMsg createLogin() {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = new String(RcConfig.GetUser() + "\n" + RcConfig.GetPassword() + "\n" + RcConfig.GetForceLogin())
				.getBytes();
		rt.type = SendMsgType.LOGIN;
		rt.make();
		return rt;
	}

	/**
	 * ������ͼ��Ϣ
	 * 
	 * @param width
	 *            ����ͼ���
	 * @return
	 */
	public static RcSendMsg createScreenShot(String width) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = width.getBytes();
		rt.type = SendMsgType.SCREENSHOT;
		rt.make();
		return rt;
	}

	/**
	 * �����ն���Ϣ
	 * 
	 * @param cmd
	 *            �����ַ���
	 * @return
	 */
	public static RcSendMsg createTerminal(String cmd) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = cmd.getBytes();
		rt.type = SendMsgType.TERMINAL;
		rt.make();
		return rt;
	}

	/**
	 * �����ǳ���Ϣ
	 * 
	 * @return
	 */
	public static RcSendMsg createLogout() {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = "logout".getBytes();
		rt.type = SendMsgType.LOGOUT;
		rt.make();
		return rt;
	}

	/**
	 * ����������Ϣ
	 * 
	 * @param cmd
	 *            �����ַ���
	 * @return
	 */
	public static RcSendMsg createCommand(String cmd) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = cmd.getBytes();
		rt.type = SendMsgType.COMMAND;
		rt.make();
		return rt;
	}

	/**
	 * ����������Ϣ
	 * 
	 * @param cmd
	 *            ��������
	 * @return
	 */
	public static RcSendMsg createTask(String cmd) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = cmd.getBytes();
		rt.type = SendMsgType.TASK;
		rt.make();
		return rt;
	}

	/**
	 * ������ʾ��Ϣ
	 * 
	 * @param str
	 *            ��ʾ�ַ���
	 * @return
	 */
	public static RcSendMsg createNotice(String str) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = str.getBytes();
		rt.type = SendMsgType.NOTICE;
		rt.make();
		return rt;
	}

	/**
	 * ����������Ϣ
	 * 
	 * @param str
	 *            �����ַ���
	 * @return
	 */
	public static RcSendMsg createWarning(String str) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = str.getBytes();
		rt.type = SendMsgType.WARNING;
		rt.make();
		return rt;
	}

	/**
	 * ����������Ϣ
	 * 
	 * @param func
	 *            ��������
	 * @return
	 */
	public static RcSendMsg createFunction(String func) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = func.getBytes();
		rt.type = SendMsgType.FUNCTION;
		rt.make();
		return rt;
	}

	/**
	 * ������ִ��Ϣ
	 * 
	 * @param id
	 *            ��ϢID
	 * @return
	 */
	public static RcSendMsg createCallback(String id) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = new String(id).getBytes();
		rt.type = SendMsgType.CALLBACK;
		rt.make();
		return rt;
	}

	/**
	 * �����ļ���Ϣ
	 * 
	 * @param msg
	 *            ��Ϣ����
	 * @return
	 */
	public static RcSendMsg createFile(String msg) {
		RcSendMsg rt = new RcSendMsg();
		rt.buf = msg.getBytes();
		rt.type = SendMsgType.FILE;
		rt.make();
		return rt;
	}

	/**
	 * ������Ϣ����
	 */
	public void make() {
		id = ++ID;
		IdString = String.format("%010d", id);
		String t = IdString + getTypeNumber(type);
		// RcDebug.v("debug", "Make String:" + t);
		byte[] n = arraycat(t.getBytes(), buf);
		// RcDebug.v("debug", "new byte[]:" + n.length);
		pack = new DatagramPacket(n, n.length);
		RcDebug.Log("Make msg:" + t + buf);
	}

	/**
	 * ���������ֽ�����
	 * 
	 * @param buf1
	 *            ����1
	 * @param buf2
	 *            ����2
	 * @return �µ�����12
	 */
	private byte[] arraycat(byte[] buf1, byte[] buf2) {
		byte[] bufret = null;
		int len1 = 0;
		int len2 = 0;
		if (buf1 != null)
			len1 = buf1.length;
		if (buf2 != null)
			len2 = buf2.length;
		if (len1 + len2 > 0)
			bufret = new byte[len1 + len2];
		if (len1 > 0)
			System.arraycopy(buf1, 0, bufret, 0, len1);
		if (len2 > 0)
			System.arraycopy(buf2, 0, bufret, len1, len2);
		return bufret;
	}

	/**
	 * ������Ϣ����ת��Ϊ�ַ���
	 * 
	 * @param type
	 *            ��Ϣ����
	 * @return �������ӵ��ַ���
	 */
	private String getTypeNumber(SendMsgType type) {
		switch (type) {
		case COMMAND:
			return "01";
		case NOTICE:
			return "02";
		case FILE:
			return "03";
		case WARNING:
			return "04";
		case TERMINAL:
			return "05";
		case FUNCTION:
			return "06";
		case CALLBACK:
			return "07";
		case LOGIN:
			return "08";
		case LOGOUT:
			return "09";
		case SCREENSHOT:
			return "10";
		case TASK:
			return "11";
		default:
			return "00";
		}
	}
}
