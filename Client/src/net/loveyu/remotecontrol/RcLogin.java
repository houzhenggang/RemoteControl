package net.loveyu.remotecontrol;

/**
 * ��¼������
 * 
 * @author loveyu
 * 
 */
public class RcLogin {
	/**
	 * ��¼ʵ��
	 */
	private static RcLogin instance = new RcLogin();

	/**
	 * ˽�й��캯��
	 */
	private RcLogin() {
	}

	/**
	 * ��ȡʵ��
	 * 
	 * @return ʵ��
	 */
	public static RcLogin Get() {
		return instance;
	}

	/**
	 * �û�SID
	 */
	public String sid = "";
	/**
	 * ��¼״̬��
	 */
	public int status = 0;
	/**
	 * ��¼��ʾ��Ϣ
	 */
	public String info = "";

	/**
	 * ���õ�¼״̬
	 * 
	 * @param status
	 *            ״̬��
	 * @param info
	 *            ��ʾ��Ϣ
	 * @return ״̬��
	 */
	public int Set(String status, String info) {
		RcDebug.Log("Login msg: status : " + status + "," + info);
		try {
			this.status = Integer.parseInt(status);
			if (this.status == 200) {
				sid = info;
			} else {
				sid = "";
				this.info = info;
			}
		} catch (Exception ex) {
			sid = "";
			this.info = info;
		}
		return this.status;
	}

	/**
	 * �����¼��Ϣ��״̬��
	 */
	public void Clear() {
		sid = info = "";
		status = 0;
	}
}
