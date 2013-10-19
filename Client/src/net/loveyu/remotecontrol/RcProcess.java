package net.loveyu.remotecontrol;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.List;

/**
 * ��Ϣ���ദ����
 * 
 * @author loveyu
 * 
 */
public class RcProcess {
	/**
	 * ��Ϣ����
	 */
	RcMessage msg;

	/**
	 * ͨ��ԭʼ���ݰ���ʼ�����߳�
	 * 
	 * @param pack
	 *            ԭʼ����
	 */
	public void Run(DatagramPacket pack) {
		RcDebug.v("debug", "received pack:" + pack.getLength());
		msg = new RcMessage(pack);
		if (msg.Status == false) {
			RcDebug.v("debug", "received a error pack");
			return;
		}
		if (msg.type != MessageType.Callback) {
			Callback();
			RcDebug.Log("Get Message:" + msg.type + ",Content:" + msg.content);
		}
		// RcDebug.v("debug", "Get Message:" + msg.content + " | type:" +
		// msg.type);
		switch (msg.type) {
		case Callback:
			RemoveQueue();
			break;
		case Text:
			Text();
			break;
		case Login:
			Login();
			break;
		case RunError:
			RunError();
			break;
		case File:
			FileProcess();
			break;
		case Picture:
			Picture();
			break;
		case Terminal:
			Terminal();
			break;
		case TerminalError:
			TerminalError();
			break;
		case Task:
			Task();
			break;
		default:
			break;
		}
	}

	/**
	 * ��ͼ����
	 */
	private void Picture() {
		String[] ps = msg.content.split("\\n");
		if (ps.length == 2) {
			String[] f = ps[0].split("\\t");
			String[] s = ps[1].split("\\t");
			if (f.length == 2 && s.length == 2) {
				PagerScreenShots.AddListItme(f[0], f[1], s[0], s[1]);
				return;
			}
		}
		RcDebug.tN("Received screenshot error");
	}

	/**
	 * �ı���Ϣ����
	 */
	private void Text() {
		PagerMessage.appendText(msg.content);
	}

	/**
	 * ������Ϣ����
	 */
	private void RunError() {
		PagerErrorMessage.appendText(msg.content);
		RcDebug.tN("������Ϣ:" + msg.content);
	}

	/**
	 * ��ִ��Ϣ����
	 */
	private void Callback() {
		RcDebug.v("debug", "return callbak:" + msg.id);
		RcNet.Get().Send(RcSendMsg.createCallback(msg.id));
	}

	/**
	 * �Ƴ����Ͷ���
	 */
	private void RemoveQueue() {
		RcDebug.v("debug", "remove callbak:" + msg.id);
		RcQueue.Get().Remove(msg.content);
	}

	/**
	 * �ļ���������
	 */
	private void FileProcess() {

		try {
			int index;
			String[] head;
			if ((index = msg.content.indexOf('\n')) != -1) {
				head = msg.content.substring(0, index).split("\\t");
			} else {
				head = msg.content.split("\\t");
			}
			if ("file".equals(head[0].toLowerCase())) {
				if (head.length == 4) {
					RcFile.Download(head[1], head[2], head[3]);
				}
			} else if ("dir".equals(head[0].toLowerCase())) {
				List<HashMap<String, String>> info = null;
				if (head.length != 6 || (info = RcFile.ParseDirInfo(msg.content.substring(index + 1))) == null) {
					RcDebug.tN("Get Dir Error");
					return;
				}
				ActivityFile.setList(info, head[5]);
			} else {
				RcDebug.tN("File Action Notice:\n" + msg.content);
				PagerMessage.appendText("FILE Action:\n" + msg.content);
			}
		} catch (Exception ex) {
			RcDebug.tN("File Process Ex:" + ex.getMessage());
		}
	}

	/**
	 * �����������
	 */
	private void Task() {
		int index;
		String[] head;
		if ((index = msg.content.indexOf('\n')) != -1) {
			head = msg.content.substring(0, index).split("\\t");
		} else {
			head = msg.content.split("\\t");
		}
		if ("process".equals(head[0].toLowerCase())) {
			if (head.length == 2)
				ActivityTask
						.UpdateList(RcTask.GetTaskList(msg.content.substring(index + 1)), Integer.parseInt(head[1]));
		} else {
			RcDebug.tN("Task Notice:\n" + msg.content);
			PagerMessage.appendText("Task Action:\n" + msg.content);
		}
	}

	/**
	 * ��¼��������
	 */
	private void Login() {
		if (msg.content.length() < 5)
			return;
		int status = RcLogin.Get().Set(msg.content.substring(0, 3), msg.content.substring(4));
		if (status == 203) {
			RcDebug.tN(RcLogin.Get().info);
		}
	}

	/**
	 * �ն���Ϣ
	 */
	private void Terminal() {
		ActivityTerminal.UpData(msg.content);
	}

	/**
	 * �ն˴���
	 */
	private void TerminalError() {
		ActivityTerminal.UpErrorData(msg.content);
	}
}
