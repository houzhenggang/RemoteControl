package net.loveyu.remotecontrol;

import java.net.DatagramPacket;

/**
 * ��Ϣ����������
 * 
 * @author loveyu
 * 
 */
public class RcMessage {

	/**
	 * ��Ϣ����
	 */
	public MessageType type;
	/**
	 * ��ϢID
	 */
	public String id;
	/**
	 * ��Ϣ״̬
	 */
	public boolean Status = false;
	/**
	 * ��Ϣ����
	 */
	public String content;

	/**
	 * �����յ������ݰ���������Ϣ����
	 * 
	 * @param pack
	 *            ԭʼ���ݰ�
	 */
	public RcMessage(DatagramPacket pack) {
		String all = new String(GetBytes(pack));
		try {
			int index = all.indexOf('\n');
			String head = all.substring(0, index);
			String[] info = head.split("\t");
			if (info.length < 3)
				return;
			id = info[0] + "\t" + info[1];
			type = getType(info[2]);
			content = all.substring(index + 1);
			Status = true;
		} catch (Exception e) {
		}
	}

	/**
	 * �������תΪ�ֽ�����
	 * 
	 * @param pack
	 *            ԭʼ�����
	 * @return
	 */
	private byte[] GetBytes(DatagramPacket pack) {
		byte[] rt = new byte[pack.getLength()];
		System.arraycopy(pack.getData(), 0, rt, 0, pack.getLength());
		return rt;
	}

	/**
	 * ��ȡ��Ϣ����
	 * @param type ��Ϣ�����ַ���
	 * @return ��Ϣ����
	 */
	private MessageType getType(String type) {
		if ("01".equals(type)) {
			return MessageType.Callback;
		}
		if ("02".equals(type)) {
			return MessageType.Text;
		}
		if ("03".equals(type)) {
			return MessageType.Login;
		}
		if ("04".equals(type)) {
			return MessageType.RunError;
		}
		if ("05".equals(type)) {
			return MessageType.File;
		}
		if ("06".equals(type)) {
			return MessageType.Picture;
		}
		if ("07".equals(type)) {
			return MessageType.Terminal;
		}
		if ("08".equals(type)) {
			return MessageType.TerminalError;
		}
		if ("09".equals(type)) {
			return MessageType.Task;
		}
		return MessageType.Unknow;
	}
}
